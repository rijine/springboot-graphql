package com.yonyou.einvoice.common.agile.graphql.servlet;

import com.yonyou.einvoice.common.agile.graphql.servlet.components.GraphQLRequestBody;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.Internal;
import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Internal
public class GraphQLController {

  @Autowired
  GraphQLInvocation graphQLInvocation;
  @Autowired
  ExecutionResultHandler executionResultHandler;
  @Autowired
  JsonSerializer jsonSerializer;
  @Autowired
  @Qualifier("onlineQL")
  private GraphQL onlineQL;
  @Autowired
  @Qualifier("testQL")
  private GraphQL testQL;

  @RequestMapping(value = "${metadata.onlineiql.endpoint.graphql:onlineql}",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Object onlineQLPost(
      @RequestHeader(value = HttpHeaders.CONTENT_TYPE, required = false) String contentType,
      @RequestParam(value = "query", required = false) String query,
      @RequestParam(value = "operationName", required = false) String operationName,
      @RequestParam(value = "variables", required = false) String variablesJson,
      @RequestBody(required = false) String body,
      WebRequest webRequest) throws IOException {
    Object result = post(onlineQL, contentType, query, operationName, variablesJson, body,
        webRequest);
    return result;
  }


  @RequestMapping(value = "${metadata.onlineiql.endpoint.graphql:onlineql}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Object onlineQLGet(
      @RequestParam("query") String query,
      @RequestParam(value = "operationName", required = false) String operationName,
      @RequestParam(value = "variables", required = false) String variablesJson,
      WebRequest webRequest) {
    Object result = executeRequest(onlineQL, query, operationName, variablesJson, webRequest);
    return result;
  }


  @RequestMapping(value = "${metadata.testiql.endpoint.graphql:testql}",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Object testQLPost(
      @RequestHeader(value = HttpHeaders.CONTENT_TYPE, required = false) String contentType,
      @RequestParam(value = "query", required = false) String query,
      @RequestParam(value = "operationName", required = false) String operationName,
      @RequestParam(value = "variables", required = false) String variablesJson,
      @RequestBody(required = false) String body,
      WebRequest webRequest) throws IOException {
    Object result = post(testQL, contentType, query, operationName, variablesJson, body,
        webRequest);
    return result;
  }

  @RequestMapping(value = "${metadata.testiql.endpoint.graphql:testql}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Object testQLGet(
      @RequestParam("query") String query,
      @RequestParam(value = "operationName", required = false) String operationName,
      @RequestParam(value = "variables", required = false) String variablesJson,
      WebRequest webRequest) {
    Object result = executeRequest(testQL, query, operationName, variablesJson, webRequest);
    return result;
  }


  private Object post(
      GraphQL graphQL,
      String contentType,
      String query,
      String operationName,
      String variablesJson,
      String body,
      WebRequest webRequest) throws IOException {

    if (body == null) {
      body = "";
    }

    if (MediaType.APPLICATION_JSON_VALUE.equals(contentType)) {
      GraphQLRequestBody request = jsonSerializer.deserialize(body, GraphQLRequestBody.class);
      if (request.getQuery() == null) {
        request.setQuery("");
      }
      return executeRequest(graphQL, request.getQuery(), request.getOperationName(),
          request.getVariables(), webRequest);
    }
    if (query != null) {
      return executeRequest(graphQL, query, operationName, variablesJson, webRequest);
    }
    if ("application/graphql".equals(contentType)) {
      return executeRequest(graphQL, body, null, null, webRequest);
    }

    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
        "Could not process GraphQL request");
  }

  private Object get(
      GraphQL graphQL,
      String query,
      String operationName,
      String variablesJson,
      WebRequest webRequest) {
    return executeRequest(graphQL, query, operationName, variablesJson,
        webRequest);
  }

  private Object executeRequest(GraphQL graphQL,
      String query,
      String operationName,
      String variables,
      WebRequest webRequest) {
    Map<String, Object> variableMap = jsonSerializer.deserialize(variables, Map.class);
    GraphQLInvocationData invocationData = new GraphQLInvocationData(query, operationName,
        variableMap);
    ExecutionResult executionResult = graphQLInvocation.invoke(graphQL, invocationData, webRequest);
    return executionResultHandler.handleExecutionResult(executionResult);
  }

}
