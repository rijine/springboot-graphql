package com.yonyou.einvoice.common.agile.enums;

import lombok.Getter;

@Getter
public enum AggrEnum {
  /**
   * 求和
   */
  SUM("sum"),
  /**
   * 求数量
   */
  COUNT("count");
  private String code;

  AggrEnum(String code) {
    this.code = code;
  }
}
