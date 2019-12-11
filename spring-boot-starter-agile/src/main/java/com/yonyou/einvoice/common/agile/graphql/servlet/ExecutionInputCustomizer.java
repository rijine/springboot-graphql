package com.yonyou.einvoice.common.agile.graphql.servlet;

import graphql.ExecutionInput;
import java.util.concurrent.CompletableFuture;
import org.springframework.web.context.request.WebRequest;

public interface ExecutionInputCustomizer {

  CompletableFuture<ExecutionInput> customizeExecutionInput(ExecutionInput executionInput,
      WebRequest webRequest);

}
