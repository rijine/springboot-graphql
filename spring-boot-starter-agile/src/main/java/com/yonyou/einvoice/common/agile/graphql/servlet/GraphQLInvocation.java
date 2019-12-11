package com.yonyou.einvoice.common.agile.graphql.servlet;

import graphql.ExecutionResult;
import graphql.GraphQL;
import org.springframework.web.context.request.WebRequest;

public interface GraphQLInvocation {

  ExecutionResult invoke(GraphQL graphQL, GraphQLInvocationData invocationData,
      WebRequest webRequest);

}
