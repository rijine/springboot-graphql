package com.yonyou.einvoice.common.agile.entity;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SqlEntity {

  private String sql;
  private Map<String, Object> paramMap;
}
