package com.yonyou.einvoice.common.agile.enums;

import lombok.Getter;

@Getter
public enum DirectionEnum {
  /**
   * 升序
   */
  ASC("asc"),
  /**
   * 降序
   */
  DESC("desc");
  String code;

  DirectionEnum(String code) {
    this.code = code;
  }
}
