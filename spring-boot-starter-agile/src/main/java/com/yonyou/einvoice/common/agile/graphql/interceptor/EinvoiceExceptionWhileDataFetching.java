package com.yonyou.einvoice.common.agile.graphql.interceptor;


import static graphql.Assert.assertNotNull;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorHelper;
import graphql.PublicApi;
import graphql.execution.ExecutionPath;
import graphql.language.SourceLocation;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This graphql error will be used if a runtime exception is encountered while a data fetcher is
 * invoked
 */
@PublicApi
public class EinvoiceExceptionWhileDataFetching implements GraphQLError {

  private final String message;
  private final List<Object> path;
  private final Throwable exception;
  private final List<SourceLocation> locations;
  private final Map<String, Object> extensions;

  public EinvoiceExceptionWhileDataFetching(ExecutionPath path, Throwable exception,
      SourceLocation sourceLocation) {
    this.path = assertNotNull(path).toList();
    this.exception = assertNotNull(exception);
    this.locations = Collections.singletonList(sourceLocation);
    this.extensions = mkExtensions(exception);
    this.message = mkMessage(path, exception);
  }

  private String mkMessage(ExecutionPath path, Throwable exception) {
    return exception.getMessage();
  }

  private Map<String, Object> mkExtensions(Throwable exception) {
    Map<String, Object> extensions = null;
    if (exception instanceof GraphQLError) {
      Map<String, Object> map = ((GraphQLError) exception).getExtensions();
      if (map != null) {
        extensions = new LinkedHashMap<>();
        extensions.putAll(map);
      }
    }
    return extensions;
  }

  public Throwable getException() {
    return exception;
  }


  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public List<SourceLocation> getLocations() {
    return locations;
  }

  @Override
  public List<Object> getPath() {
    return path;
  }

  @Override
  public Map<String, Object> getExtensions() {
    return extensions;
  }

  @Override
  public ErrorType getErrorType() {
    return ErrorType.DataFetchingException;
  }

  @Override
  public String toString() {
    return "ExceptionWhileDataFetching{" +
        "path=" + path +
        "exception=" + exception +
        "locations=" + locations +
        '}';
  }

  @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
  @Override
  public boolean equals(Object o) {
    return GraphqlErrorHelper.equals(this, o);
  }

  @Override
  public int hashCode() {
    return GraphqlErrorHelper.hashCode(this);
  }
}
