package com.yonyou.einvoice.common.metadata.graphql.servlet;

import graphql.ExecutionResult;

public interface ExecutionResultHandler {

  Object handleExecutionResult(ExecutionResult executionResultCF);
}
