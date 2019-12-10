package com.yonyou.einvoice.common.metadata.graphql.strategy;

import static graphql.execution.ExecutionStepInfo.newExecutionStepInfo;
import static graphql.execution.FieldCollectorParameters.newParameters;
import static graphql.schema.DataFetchingEnvironmentBuilder.newDataFetchingEnvironment;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import com.yonyou.einvoice.common.metadata.graphql.context.AbstractAsyncTransmitContext;
import com.yonyou.einvoice.common.metadata.graphql.interceptor.EinvoiceDataFetcherExceptionHandler;
import com.yonyou.einvoice.common.metadata.graphql.node.BatchedAsyncExecutionNode;
import com.yonyou.einvoice.common.metadata.graphql.node.BatchedAsyncExecutionResult;
import com.yonyou.einvoice.common.metadata.graphql.node.FirstLayerMapOrList;
import graphql.Assert;
import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.execution.Async;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.ExecutionContext;
import graphql.execution.ExecutionContextBuilder;
import graphql.execution.ExecutionPath;
import graphql.execution.ExecutionStepInfo;
import graphql.execution.ExecutionStrategy;
import graphql.execution.ExecutionStrategyParameters;
import graphql.execution.FieldCollectorParameters;
import graphql.execution.NonNullableFieldValidator;
import graphql.execution.TypeResolutionParameters;
import graphql.execution.batched.BatchAssertionFailed;
import graphql.execution.batched.BatchedDataFetcher;
import graphql.execution.batched.BatchedDataFetcherFactory;
import graphql.execution.batched.FetchedValue;
import graphql.execution.batched.FetchedValues;
import graphql.execution.batched.MapOrList;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionStrategyParameters;
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters;
import graphql.execution.instrumentation.parameters.InstrumentationFieldParameters;
import graphql.language.Field;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.DataFetchingFieldSelectionSetImpl;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeUtil;
import graphql.schema.GraphQLUnionType;
import graphql.schema.visibility.GraphqlFieldVisibility;
import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

/**
 * 批量+异步处理策略
 *
 * @author liuqiangm
 */
@SuppressWarnings("all")
public class BatchedAsyncExecutionStrategy extends ExecutionStrategy {

  public AbstractAsyncTransmitContext asyncTransmitContext;

  /**
   * graphql执行线程池设置。 默认开启核心数相同的核心线程数 最大开启20线程 超出线程数后，由主线程负责继续执行
   */
  private static final ExecutorService executorService = new ThreadPoolExecutor(
      Runtime.getRuntime().availableProcessors(),
      20,
      5L, TimeUnit.SECONDS,
      new SynchronousQueue<Runnable>(),
      new GraphQLThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());

  private final BatchedDataFetcherFactory batchingFactory = new BatchedDataFetcherFactory();

  public BatchedAsyncExecutionStrategy() {
    this(new EinvoiceDataFetcherExceptionHandler());
  }

  public BatchedAsyncExecutionStrategy(DataFetcherExceptionHandler dataFetcherExceptionHandler) {
    super(dataFetcherExceptionHandler);
  }

  private ExecutionContext duplicateExecutionContext(ExecutionContext executionContext) {
    ExecutionContext context = new ExecutionContextBuilder()
        .instrumentation(executionContext.getInstrumentation())
        .executionId(executionContext.getExecutionId())
        .graphQLSchema(executionContext.getGraphQLSchema())
        .instrumentationState(executionContext.getInstrumentationState())
        .queryStrategy(executionContext.getQueryStrategy())
        .mutationStrategy(executionContext.getMutationStrategy())
        .subscriptionStrategy(executionContext.getSubscriptionStrategy())
        .fragmentsByName(executionContext.getFragmentsByName())
        .document(executionContext.getDocument())
        .operationDefinition(executionContext.getOperationDefinition())
        .variables(executionContext.getVariables())
        .context(executionContext.getContext())
        .root(executionContext.getRoot())
        .dataLoaderRegistry(executionContext.getDataLoaderRegistry()).build();
    return context;
  }

  @Override
  public CompletableFuture<ExecutionResult> execute(ExecutionContext executionContext,
      ExecutionStrategyParameters parameters) {
    InstrumentationContext<ExecutionResult> executionStrategyCtx = executionContext
        .getInstrumentation()
        .beginExecutionStrategy(
            new InstrumentationExecutionStrategyParameters(executionContext, parameters));

    GraphQLObjectType type = (GraphQLObjectType) parameters.getExecutionStepInfo()
        .getUnwrappedNonNullType();

    BatchedAsyncExecutionNode root = new BatchedAsyncExecutionNode(type,
        parameters.getExecutionStepInfo(),
        parameters.getFields(),
        singletonList(FirstLayerMapOrList.createMap(new LinkedHashMap<>())),
        Collections.singletonList(parameters.getSource())
    );

    List<String> fieldNames = new ArrayList<>(parameters.getFields().keySet());
    /**
     * 如果只包含单个查询，则走单线程，不走线程池
     */
    if (fieldNames.size() == 1) {
      Queue<BatchedAsyncExecutionNode> nodes = new ArrayDeque<>();
      CompletableFuture<ExecutionResult> result = new CompletableFuture<>();
      executeImpl(executionContext,
          parameters,
          root,
          root,
          nodes,
          root.getFields().keySet().iterator(),
          result, false);

      executionStrategyCtx.onDispatched(result);
      result.whenComplete(executionStrategyCtx::onCompleted);
      return result;
    }
    List<CompletableFuture> futures = new ArrayList<>(fieldNames.size());
    Map<String, Object> map = new TreeMap<>();
    if (asyncTransmitContext != null) {
      map.put("context", asyncTransmitContext.getMasterThreadContext());
    }
    for (String fieldName : fieldNames) {
      final CompletableFuture<ExecutionResult> result = new CompletableFuture<>();
      Runnable runnable = () -> {
        try {
          if (asyncTransmitContext != null) {
            asyncTransmitContext.setSubThreadContext(map.get("context"), fieldName);
          }
          Queue<BatchedAsyncExecutionNode> nodes = new ArrayDeque<>();
          executeImpl(duplicateExecutionContext(executionContext),
              parameters,
              root,
              root,
              nodes,
              Collections.singletonList(fieldName).iterator(),
              result, true);
          executionStrategyCtx.onDispatched(result);
          result.whenComplete(executionStrategyCtx::onCompleted);
        } finally {
          if (asyncTransmitContext != null) {
            asyncTransmitContext.resetSubThreadContext();
          }
        }
      };
      futures.add(result);
      executorService.execute(runnable);
    }

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    BatchedAsyncExecutionResult executionResult = new BatchedAsyncExecutionResult();
    for (CompletableFuture<ExecutionResult> resultFuture : futures) {
      ExecutionResult result = null;
      try {
        result = resultFuture.get();
        // 错误信息聚合
        executionResult.getErrors().addAll(result.getErrors());
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (ExecutionException e) {
        e.printStackTrace();
      }
    }
    // 结果按顺序聚合
    Map<String, Object> tmpMap = (Map) root.getParentResults().get(0).toObject();
    fieldNames.forEach(fieldName -> {
      ((Map) executionResult.getData()).put(fieldName, tmpMap.get(fieldName));
    });
    return CompletableFuture.completedFuture(executionResult);
  }


  private void executeImpl(ExecutionContext executionContext,
      ExecutionStrategyParameters parameters,
      BatchedAsyncExecutionNode root,
      BatchedAsyncExecutionNode curNode,
      Queue<BatchedAsyncExecutionNode> queueOfNodes,
      Iterator<String> curFieldNames,
      CompletableFuture<ExecutionResult> overallResult, boolean multiThread) {

    if (!curFieldNames.hasNext() && queueOfNodes.isEmpty()) {
      if (multiThread) {
        overallResult.complete(new ExecutionResultImpl(null,
            executionContext.getErrors()));
      } else {
        overallResult.complete(new ExecutionResultImpl(root.getParentResults().get(0).toObject(),
            executionContext.getErrors()));
      }
      return;
    }

    if (!curFieldNames.hasNext()) {
      curNode = queueOfNodes.poll();
      curFieldNames = curNode.getFields().keySet().iterator();
    }

    String fieldName = curFieldNames.next();
    List<Field> currentField = curNode.getFields().get(fieldName);

    ExecutionStepInfo currentParentExecutionStepInfo = parameters.getExecutionStepInfo();
    ExecutionStepInfo newParentExecutionStepInfo = newExecutionStepInfo()
        .type(curNode.getType())
        .fieldDefinition(currentParentExecutionStepInfo.getFieldDefinition())
        .field(currentParentExecutionStepInfo.getField())
        .path(currentParentExecutionStepInfo.getPath())
        .parentInfo(currentParentExecutionStepInfo.getParent())
        .build();

    ExecutionPath fieldPath = curNode.getExecutionStepInfo().getPath()
        .segment(mkNameForPath(currentField));
    GraphQLFieldDefinition fieldDefinition = getFieldDef(executionContext.getGraphQLSchema(),
        curNode.getType(), currentField.get(0));

    ExecutionStepInfo executionStepInfo = newExecutionStepInfo()
        .type(fieldDefinition.getType())
        .fieldDefinition(fieldDefinition)
        .field(currentField.get(0))
        .path(fieldPath)
        .parentInfo(newParentExecutionStepInfo)
        .build();

    ExecutionStrategyParameters newParameters = parameters
        .transform(builder -> builder
            .path(fieldPath)
            .field(currentField)
            .executionStepInfo(executionStepInfo)
        );

    BatchedAsyncExecutionNode finalCurNode = curNode;
    Iterator<String> finalCurFieldNames = curFieldNames;

    resolveField(executionContext, newParameters, fieldName, curNode)
        .whenComplete((childNodes, exception) -> {
          if (exception != null) {
            handleNonNullException(executionContext, overallResult, exception);
            return;
          }
          queueOfNodes.addAll(childNodes);
          executeImpl(executionContext, newParameters, root, finalCurNode, queueOfNodes,
              finalCurFieldNames, overallResult, multiThread);
        });
  }


  private CompletableFuture<List<BatchedAsyncExecutionNode>> resolveField(
      ExecutionContext executionContext,
      ExecutionStrategyParameters parameters,
      String fieldName,
      BatchedAsyncExecutionNode node) {
    GraphQLObjectType parentType = node.getType();
    List<Field> fields = node.getFields().get(fieldName);

    GraphQLFieldDefinition fieldDef = getFieldDef(executionContext.getGraphQLSchema(), parentType,
        fields.get(0));

    Instrumentation instrumentation = executionContext.getInstrumentation();
    ExecutionStepInfo executionStepInfo = parameters.getExecutionStepInfo();
    InstrumentationContext<ExecutionResult> fieldCtx = instrumentation.beginField(
        new InstrumentationFieldParameters(executionContext, fieldDef, executionStepInfo)
    );

    CompletableFuture<FetchedValues> fetchedData = fetchData(executionContext, parameters,
        fieldName, node, fieldDef);

    CompletableFuture<List<BatchedAsyncExecutionNode>> result = fetchedData
        .thenApply((fetchedValues) -> {

          GraphqlFieldVisibility fieldVisibility = executionContext.getGraphQLSchema()
              .getFieldVisibility();
          Map<String, Object> argumentValues = valuesResolver.getArgumentValues(
              fieldVisibility,
              fieldDef.getArguments(), fields.get(0).getArguments(),
              executionContext.getVariables());

          return completeValues(executionContext, fetchedValues, executionStepInfo, fieldName,
              fields,
              argumentValues);
        });
    fieldCtx.onDispatched(null);
    result = result.whenComplete((nodes, throwable) -> fieldCtx.onCompleted(null, throwable));
    return result;

  }

  private CompletableFuture<FetchedValues> fetchData(ExecutionContext executionContext,
      ExecutionStrategyParameters parameters,
      String fieldName,
      BatchedAsyncExecutionNode node,
      GraphQLFieldDefinition fieldDef) {
    GraphQLObjectType parentType = node.getType();
    List<Field> fields = node.getFields().get(fieldName);
    List<MapOrList> parentResults = node.getParentResults();

    GraphqlFieldVisibility fieldVisibility = executionContext.getGraphQLSchema()
        .getFieldVisibility();
    Map<String, Object> argumentValues = valuesResolver.getArgumentValues(
        fieldVisibility,
        fieldDef.getArguments(), fields.get(0).getArguments(), executionContext.getVariables());

    GraphQLOutputType fieldType = fieldDef.getType();
    DataFetchingFieldSelectionSet fieldCollector = DataFetchingFieldSelectionSetImpl
        .newCollector(executionContext, fieldType, fields);

    DataFetchingEnvironment environment = newDataFetchingEnvironment(executionContext)
        .source(node.getSources())
        .arguments(argumentValues)
        .fieldDefinition(fieldDef)
        .fields(fields)
        .fieldType(fieldDef.getType())
        .executionStepInfo(parameters.getExecutionStepInfo())
        .parentType(parentType)
        .selectionSet(fieldCollector)
        .build();

    Instrumentation instrumentation = executionContext.getInstrumentation();
    InstrumentationFieldFetchParameters instrumentationFieldFetchParameters =
        new InstrumentationFieldFetchParameters(executionContext, fieldDef, environment,
            parameters);
    InstrumentationContext<Object> fetchCtx = instrumentation
        .beginFieldFetch(instrumentationFieldFetchParameters);

    CompletableFuture<Object> fetchedValue;
    try {
      DataFetcher<?> dataFetcher = instrumentation.instrumentDataFetcher(
          getDataFetcher(fieldDef), instrumentationFieldFetchParameters);
      Object fetchedValueRaw = dataFetcher.get(environment);
      fetchedValue = Async.toCompletableFuture(fetchedValueRaw);
    } catch (Exception e) {
      fetchedValue = new CompletableFuture<>();
      fetchedValue.completeExceptionally(e);
    }
    return fetchedValue
        .thenApply((result) -> assertResult(parentResults, result))
        .whenComplete(fetchCtx::onCompleted)
        .handle(handleResult(executionContext, parameters, parentResults, fields, fieldDef,
            argumentValues, environment));
  }

  private BiFunction<List<Object>, Throwable, FetchedValues> handleResult(
      ExecutionContext executionContext, ExecutionStrategyParameters parameters,
      List<MapOrList> parentResults, List<Field> fields, GraphQLFieldDefinition fieldDef,
      Map<String, Object> argumentValues, DataFetchingEnvironment environment) {
    return (result, exception) -> {
      if (exception != null) {
        if (exception instanceof CompletionException) {
          exception = exception.getCause();
        }
        DataFetcherExceptionHandlerParameters handlerParameters = DataFetcherExceptionHandlerParameters
            .newExceptionParameters()
            .executionContext(executionContext)
            .dataFetchingEnvironment(environment)
            .argumentValues(argumentValues)
            .field(fields.get(0))
            .fieldDefinition(fieldDef)
            .path(parameters.getPath())
            .exception(exception)
            .build();
        dataFetcherExceptionHandler.accept(handlerParameters);
        result = Collections.nCopies(parentResults.size(), null);
      }
      List<Object> values = result;
      List<FetchedValue> retVal = new ArrayList<>();
      for (int i = 0; i < parentResults.size(); i++) {
        Object value = unboxPossibleOptional(values.get(i));
        retVal.add(new FetchedValue(parentResults.get(i), value));
      }
      return new FetchedValues(retVal, parameters.getExecutionStepInfo(), parameters.getPath());
    };
  }

  private List<Object> assertResult(List<MapOrList> parentResults, Object result) {
    result = convertPossibleArray(result);
    if (result != null && !(result instanceof Iterable)) {
      throw new BatchAssertionFailed(String.format(
          "BatchedDataFetcher provided an invalid result: Iterable expected but got '%s'. Affected fields are set to null.",
          result.getClass().getName()));
    }
    @SuppressWarnings("unchecked")
    Iterable<Object> iterableResult = (Iterable<Object>) result;
    if (iterableResult == null) {
      throw new BatchAssertionFailed(
          "BatchedDataFetcher provided a null Iterable of result values. Affected fields are set to null.");
    }
    List<Object> resultList = new ArrayList<>();
    iterableResult.forEach(resultList::add);

    long size = resultList.size();
    if (size != parentResults.size()) {
      throw new BatchAssertionFailed(String.format(
          "BatchedDataFetcher provided invalid number of result values, expected %d but got %d. Affected fields are set to null.",
          parentResults.size(), size));
    }
    return resultList;
  }

  private List<BatchedAsyncExecutionNode> completeValues(ExecutionContext executionContext,
      FetchedValues fetchedValues, ExecutionStepInfo executionStepInfo,
      String fieldName, List<Field> fields,
      Map<String, Object> argumentValues) {

    handleNonNullType(executionContext, fetchedValues);

    GraphQLType unwrappedFieldType = executionStepInfo.getUnwrappedNonNullType();

    if (isPrimitive(unwrappedFieldType)) {
      handlePrimitives(fetchedValues, fieldName, unwrappedFieldType);
      return Collections.emptyList();
    } else if (isObject(unwrappedFieldType)) {
      return handleObject(executionContext, argumentValues, fetchedValues, fieldName, fields,
          executionStepInfo);
    } else if (isList(unwrappedFieldType)) {
      return handleList(executionContext, argumentValues, fetchedValues, fieldName, fields,
          executionStepInfo);
    } else {
      return Assert.assertShouldNeverHappen("can't handle type: %s", unwrappedFieldType);
    }
  }

  @SuppressWarnings("unchecked")
  private List<BatchedAsyncExecutionNode> handleList(ExecutionContext executionContext,
      Map<String, Object> argumentValues,
      FetchedValues fetchedValues, String fieldName, List<Field> fields,
      ExecutionStepInfo executionStepInfo) {

    GraphQLList listType = (GraphQLList) executionStepInfo.getUnwrappedNonNullType();
    List<FetchedValue> flattenedValues = new ArrayList<>();

    for (FetchedValue value : fetchedValues.getValues()) {
      MapOrList mapOrList = value.getParentResult();

      if (value.getValue() == null) {
        mapOrList.putOrAdd(fieldName, null);
        continue;
      }

      MapOrList listResult = mapOrList.createAndPutList(fieldName);
      for (Object rawValue : toIterable(value.getValue())) {
        rawValue = unboxPossibleOptional(rawValue);
        flattenedValues.add(new FetchedValue(listResult, rawValue));
      }
    }
    GraphQLOutputType innerSubType = (GraphQLOutputType) listType.getWrappedType();
    ExecutionStepInfo newExecutionStepInfo = executionStepInfo
        .changeTypeWithPreservedNonNull(GraphQLTypeUtil.unwrapNonNull(innerSubType));
    FetchedValues flattenedFetchedValues = new FetchedValues(flattenedValues, newExecutionStepInfo,
        fetchedValues.getPath());

    return completeValues(executionContext, flattenedFetchedValues, newExecutionStepInfo, fieldName,
        fields, argumentValues);
  }

  @SuppressWarnings("UnnecessaryLocalVariable")
  private List<BatchedAsyncExecutionNode> handleObject(ExecutionContext executionContext,
      Map<String, Object> argumentValues,
      FetchedValues fetchedValues, String fieldName, List<Field> fields,
      ExecutionStepInfo executionStepInfo) {

    // collect list of values by actual type (needed because of interfaces and unions)
    Map<GraphQLObjectType, List<MapOrList>> resultsByType = new LinkedHashMap<>();
    Map<GraphQLObjectType, List<Object>> sourceByType = new LinkedHashMap<>();

    for (FetchedValue value : fetchedValues.getValues()) {
      MapOrList mapOrList = value.getParentResult();
      if (value.getValue() == null) {
        mapOrList.putOrAdd(fieldName, null);
        continue;
      }
      MapOrList childResult = mapOrList.createAndPutMap(fieldName);

      GraphQLObjectType resolvedType = getGraphQLObjectType(executionContext, fields.get(0),
          executionStepInfo.getUnwrappedNonNullType(), value.getValue(), argumentValues);
      resultsByType.putIfAbsent(resolvedType, new ArrayList<>());
      resultsByType.get(resolvedType).add(childResult);

      sourceByType.putIfAbsent(resolvedType, new ArrayList<>());
      sourceByType.get(resolvedType).add(value.getValue());
    }

    List<BatchedAsyncExecutionNode> childNodes = new ArrayList<>();
    for (GraphQLObjectType resolvedType : resultsByType.keySet()) {
      List<MapOrList> results = resultsByType.get(resolvedType);
      List<Object> sources = sourceByType.get(resolvedType);
      Map<String, List<Field>> childFields = getChildFields(executionContext, resolvedType, fields);

      ExecutionStepInfo newExecutionStepInfo = executionStepInfo
          .changeTypeWithPreservedNonNull(resolvedType);

      childNodes.add(
          new BatchedAsyncExecutionNode(resolvedType, newExecutionStepInfo, childFields, results,
              sources));
    }
    return childNodes;
  }


  private void handleNonNullType(ExecutionContext executionContext, FetchedValues fetchedValues) {

    ExecutionStepInfo executionStepInfo = fetchedValues.getExecutionStepInfo();
    NonNullableFieldValidator nonNullableFieldValidator = new NonNullableFieldValidator(
        executionContext, executionStepInfo);
    ExecutionPath path = fetchedValues.getPath();
    for (FetchedValue value : fetchedValues.getValues()) {
      nonNullableFieldValidator.validate(path, value.getValue());
    }
  }

  private Map<String, List<Field>> getChildFields(ExecutionContext executionContext,
      GraphQLObjectType resolvedType,
      List<Field> fields) {

    FieldCollectorParameters collectorParameters = newParameters()
        .schema(executionContext.getGraphQLSchema())
        .objectType(resolvedType)
        .fragments(executionContext.getFragmentsByName())
        .variables(executionContext.getVariables())
        .build();

    return fieldCollector.collectFields(collectorParameters, fields);
  }

  private GraphQLObjectType getGraphQLObjectType(ExecutionContext executionContext, Field field,
      GraphQLType fieldType, Object value, Map<String, Object> argumentValues) {
    GraphQLObjectType resolvedType = null;
    if (fieldType instanceof GraphQLInterfaceType) {
      resolvedType = resolveTypeForInterface(TypeResolutionParameters.newParameters()
          .graphQLInterfaceType((GraphQLInterfaceType) fieldType)
          .field(field)
          .value(value)
          .argumentValues(argumentValues)
          .context(executionContext.getContext())
          .schema(executionContext.getGraphQLSchema())
          .build());
    } else if (fieldType instanceof GraphQLUnionType) {
      resolvedType = resolveTypeForUnion(TypeResolutionParameters.newParameters()
          .graphQLUnionType((GraphQLUnionType) fieldType)
          .field(field)
          .value(value)
          .argumentValues(argumentValues)
          .context(executionContext.getContext())
          .schema(executionContext.getGraphQLSchema())
          .build());
    } else if (fieldType instanceof GraphQLObjectType) {
      resolvedType = (GraphQLObjectType) fieldType;
    }
    return resolvedType;
  }

  private void handlePrimitives(FetchedValues fetchedValues, String fieldName,
      GraphQLType fieldType) {
    for (FetchedValue value : fetchedValues.getValues()) {
      Object coercedValue = coerce(fieldType, value.getValue());
      //6.6.1 http://facebook.github.io/graphql/#sec-Field-entries
      if (coercedValue instanceof Double && ((Double) coercedValue).isNaN()) {
        coercedValue = null;
      }
      value.getParentResult().putOrAdd(fieldName, coercedValue);
    }
  }

  private Object coerce(GraphQLType type, Object value) {
    if (value == null) {
      return null;
    }
    if (type instanceof GraphQLEnumType) {
      return ((GraphQLEnumType) type).getCoercing().serialize(value);
    } else {
      return ((GraphQLScalarType) type).getCoercing().serialize(value);
    }
  }

  private boolean isList(GraphQLType type) {
    return type instanceof GraphQLList;
  }

  private boolean isPrimitive(GraphQLType type) {
    return type instanceof GraphQLScalarType || type instanceof GraphQLEnumType;
  }

  private boolean isObject(GraphQLType type) {
    return type instanceof GraphQLObjectType ||
        type instanceof GraphQLInterfaceType ||
        type instanceof GraphQLUnionType;
  }


  private Object convertPossibleArray(Object result) {
    if (result != null && result.getClass().isArray()) {
      return IntStream.range(0, Array.getLength(result))
          .mapToObj(i -> Array.get(result, i))
          .collect(toList());
    }
    return result;
  }

  private BatchedDataFetcher getDataFetcher(GraphQLFieldDefinition fieldDef) {
    DataFetcher supplied = fieldDef.getDataFetcher();
    return batchingFactory.create(supplied);
  }

  public void setAsyncTransmitContext(
      AbstractAsyncTransmitContext asyncTransmitContext) {
    this.asyncTransmitContext = asyncTransmitContext;
  }
}
