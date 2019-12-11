package com.yonyou.einvoice.common.agile.graphql.servlet;

public interface JsonSerializer {

  String serialize(Object object);

  <T> T deserialize(String json, Class<T> requiredType);
}
