package com.yonyou.einvoice.common.metadata.graphql.servlet.components;

import graphql.Internal;

@Internal
public class GraphQLRequestBody {

  private String query;
  private String operationName;
  private String variables;

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public String getOperationName() {
    return operationName;
  }

  public void setOperationName(String operationName) {
    this.operationName = operationName;
  }

  public String getVariables() {
    return variables;
  }

  public void setVariables(String variables) {
    this.variables = variables;
  }
}
