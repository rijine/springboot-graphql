package com.yonyou.einvoice.common.agile.graphql.servlet.components;

import com.yonyou.einvoice.common.agile.graphql.servlet.ExecutionInputCustomizer;
import graphql.ExecutionInput;
import graphql.Internal;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

@Component
@Internal
public class DefaultExecutionInputCustomizer implements ExecutionInputCustomizer {

  @Override
  public CompletableFuture<ExecutionInput> customizeExecutionInput(ExecutionInput executionInput,
      WebRequest webRequest) {
    return CompletableFuture.completedFuture(executionInput);
  }
}
