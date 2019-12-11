package com.yonyou.einvoice.common.metadata.graphql.node;

import graphql.execution.batched.MapOrList;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public class FirstLayerMapOrList extends MapOrList {


  private Map<String, Object> map;
  private List<Object> list;

  public static FirstLayerMapOrList createList(List<Object> list) {
    FirstLayerMapOrList mapOrList = new FirstLayerMapOrList();
    mapOrList.list = list;
    return mapOrList;
  }

  public static MapOrList createMap(Map<String, Object> map) {
    FirstLayerMapOrList mapOrList = new FirstLayerMapOrList();
    mapOrList.map = map;
    return mapOrList;
  }

  @Override
  public MapOrList createAndPutMap(String key) {
    Map<String, Object> map = new LinkedHashMap<>();
    putOrAdd(key, map);
    return createMap(map);
  }

  @Override
  public FirstLayerMapOrList createAndPutList(String key) {
    List<Object> resultList = new ArrayList<>();
    putOrAdd(key, resultList);
    return createList(resultList);
  }

  @Override
  public void putOrAdd(String fieldName, Object value) {
    if (this.map != null) {
      synchronized (map) {
        this.map.put(fieldName, value);
      }
    } else {
      this.list.add(value);
    }
  }


  @Override
  public Object toObject() {
    if (this.map != null) {
      return this.map;
    } else {
      return this.list;
    }
  }
}
