package com.yonyou.einvoice.common.agile.graphql.servlet.components;

import com.yonyou.einvoice.common.agile.graphql.servlet.ExecutionInputCustomizer;
import com.yonyou.einvoice.common.agile.graphql.servlet.GraphQLInvocation;
import com.yonyou.einvoice.common.agile.graphql.servlet.GraphQLInvocationData;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.Internal;
import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

@Component
@Internal
public class DefaultGraphQLInvocation implements GraphQLInvocation {

  @Autowired(required = false)
  DataLoaderRegistry dataLoaderRegistry;

  @Autowired
  ExecutionInputCustomizer executionInputCustomizer;

  @Override
  public ExecutionResult invoke(GraphQL graphQL, GraphQLInvocationData invocationData,
      WebRequest webRequest) {
    ExecutionInput.Builder executionInputBuilder = ExecutionInput.newExecutionInput()
        .query(invocationData.getQuery())
        .operationName(invocationData.getOperationName())
        .variables(invocationData.getVariables());
    if (dataLoaderRegistry != null) {
      executionInputBuilder.dataLoaderRegistry(dataLoaderRegistry);
    }
    ExecutionInput executionInput = executionInputBuilder.build();
    return graphQL.execute(executionInput);
  }

}
