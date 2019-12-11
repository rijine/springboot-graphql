package com.yonyou.einvoice.common.metadata.enums;

import com.yonyou.einvoice.common.metadata.element.Source;
import java.util.List;

public enum TypeEnum {
  /**
   * 另一条sql类型
   */
  SOURCE("source"),
  /**
   * 常量类型。（字符串常量）
   */
  CONSTANT("constant"),
  /**
   * 列表类型
   */
  LIST("list");

  String code;

  TypeEnum(String code) {
    this.code = code;
  }

  public static TypeEnum getType(Object obj) {
    if (obj == null) {
      return CONSTANT;
    }
    if (obj instanceof Source) {
      return SOURCE;
    }
    if (obj instanceof List) {
      return LIST;
    }
    // 默认类型
    return CONSTANT;
  }

}
