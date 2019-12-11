package com.yonyou.einvoice.common.agile.graphql.servlet;

import graphql.ExecutionResult;

public interface ExecutionResultHandler {

  Object handleExecutionResult(ExecutionResult executionResultCF);
}
