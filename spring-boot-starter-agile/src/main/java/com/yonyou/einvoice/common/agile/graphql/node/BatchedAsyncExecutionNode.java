package com.yonyou.einvoice.common.agile.graphql.node;

import graphql.execution.ExecutionStepInfo;
import graphql.execution.batched.MapOrList;
import graphql.language.Field;
import graphql.schema.GraphQLObjectType;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"ALL", "AliDeprecation"})
public class BatchedAsyncExecutionNode {

  private final GraphQLObjectType type;
  private final ExecutionStepInfo executionStepInfo;
  private final Map<String, List<Field>> fields;
  @SuppressWarnings("AliDeprecation")
  private final List<MapOrList> parentResults;
  private final List<Object> sources;

  @SuppressWarnings("AliDeprecation")
  public BatchedAsyncExecutionNode(GraphQLObjectType type,
      ExecutionStepInfo executionStepInfo,
      Map<String, List<Field>> fields,
      List<MapOrList> parentResults,
      List<Object> sources) {
    this.type = type;
    this.executionStepInfo = executionStepInfo;
    this.fields = fields;
    this.parentResults = parentResults;
    this.sources = sources;
  }

  public GraphQLObjectType getType() {
    return type;
  }

  public ExecutionStepInfo getExecutionStepInfo() {
    return executionStepInfo;
  }

  public Map<String, List<Field>> getFields() {
    return fields;
  }

  @SuppressWarnings("AliDeprecation")
  public List<MapOrList> getParentResults() {
    return parentResults;
  }

  public List<Object> getSources() {
    return sources;
  }
}
