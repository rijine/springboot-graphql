package com.yonyou.einvoice.common.metadata.graphql.servlet;

import graphql.ExecutionResult;
import graphql.GraphQL;
import org.springframework.web.context.request.WebRequest;

public interface GraphQLInvocation {

  ExecutionResult invoke(GraphQL graphQL, GraphQLInvocationData invocationData,
      WebRequest webRequest);

}
