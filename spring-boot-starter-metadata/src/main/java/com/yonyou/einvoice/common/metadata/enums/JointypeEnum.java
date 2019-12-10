package com.yonyou.einvoice.common.metadata.enums;

import lombok.Getter;

@Getter
public enum JointypeEnum {
  /**
   * 左连接
   */
  LEFTJOIN("left join"),
  /**
   * 右连接
   */
  RIGHTJOIN("right join"),
  /**
   * 内连接
   */
  INNERJOIN("inner join"),
  /**
   * 全连接
   */
  FULLJOIN("full join");

  String code;

  JointypeEnum(String code) {
    this.code = code;
  }
}
