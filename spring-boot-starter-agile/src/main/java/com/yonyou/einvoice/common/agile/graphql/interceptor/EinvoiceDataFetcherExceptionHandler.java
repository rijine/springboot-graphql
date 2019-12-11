package com.yonyou.einvoice.common.agile.graphql.interceptor;

import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.ExecutionPath;
import graphql.language.SourceLocation;
import lombok.extern.slf4j.Slf4j;

/**
 * 获取数据时出现异常，封装异常结果并返回。
 */
@Slf4j
public class EinvoiceDataFetcherExceptionHandler implements DataFetcherExceptionHandler {

  @Override
  public void accept(DataFetcherExceptionHandlerParameters handlerParameters) {
    Throwable exception = handlerParameters.getException();
    SourceLocation sourceLocation = handlerParameters.getField().getSourceLocation();
    ExecutionPath path = handlerParameters.getPath();
    EinvoiceExceptionWhileDataFetching error = new EinvoiceExceptionWhileDataFetching(path,
        exception, sourceLocation);
    handlerParameters.getExecutionContext().addError(error);
  }
}
