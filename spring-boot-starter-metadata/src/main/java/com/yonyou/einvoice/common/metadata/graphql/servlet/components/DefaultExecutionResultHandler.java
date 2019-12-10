package com.yonyou.einvoice.common.metadata.graphql.servlet.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonyou.einvoice.common.metadata.graphql.servlet.ExecutionResultHandler;
import graphql.ExecutionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DefaultExecutionResultHandler implements ExecutionResultHandler {

  @Autowired
  ObjectMapper objectMapper;

  @Override
  public Object handleExecutionResult(ExecutionResult executionResultCF) {
    return executionResultCF.toSpecification();
  }
}
