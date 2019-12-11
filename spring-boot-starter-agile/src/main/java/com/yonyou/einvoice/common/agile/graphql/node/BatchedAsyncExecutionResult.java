package com.yonyou.einvoice.common.agile.graphql.node;


import static java.util.stream.Collectors.toList;

import graphql.ExecutionResult;
import graphql.GraphQLError;
import graphql.Internal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Internal
@Setter
@Getter
public class BatchedAsyncExecutionResult implements ExecutionResult {

  private Object data = new LinkedHashMap();
  private List<GraphQLError> errors = new ArrayList<>();
  private Map<Object, Object> extensions;

  @Override
  public Map<String, Object> toSpecification() {
    Map<String, Object> result = new LinkedHashMap<>();
    result.put("data", data);
    if (errors != null && !errors.isEmpty()) {
      result.put("errors", errorsToSpec(errors));
    }
    if (extensions != null) {
      result.put("extensions", extensions);
    }
    return result;
  }

  private Object errorsToSpec(List<GraphQLError> errors) {
    return errors.stream().map(GraphQLError::toSpecification).collect(toList());
  }
}
