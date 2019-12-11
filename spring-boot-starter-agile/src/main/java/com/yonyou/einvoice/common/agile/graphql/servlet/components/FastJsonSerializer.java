package com.yonyou.einvoice.common.agile.graphql.servlet.components;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yonyou.einvoice.common.agile.graphql.servlet.JsonSerializer;
import org.springframework.stereotype.Component;

@Component
public class FastJsonSerializer implements JsonSerializer {

  @Override
  public String serialize(Object object) {
    return JSON.toJSONString(object, SerializerFeature.WriteMapNullValue);
  }

  @Override
  public <T> T deserialize(String json, Class<T> requiredType) {
    return JSONObject.parseObject(json, requiredType, Feature.OrderedField);
  }

}
