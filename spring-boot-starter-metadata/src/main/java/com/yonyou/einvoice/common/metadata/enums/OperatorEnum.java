package com.yonyou.einvoice.common.metadata.enums;

import lombok.Getter;

@Getter
public enum OperatorEnum {
  /**
   * =
   */
  EQUAL("="),
  /**
   * != 或 <></>
   */
  NOTEQUAL("<>"),
  /**
   * >
   */
  GREATER(">"),
  /**
   * <
   */
  LESS("<"),
  /**
   * >=
   */
  GREATEREQUAL(">="),
  /**
   * <=
   */
  LESSEQUAL("<="),
  /**
   * like
   */
  LIKE("like"),
  /**
   * in
   */
  IN("in"),
  /**
   * not in
   */
  NOTIN("not in"),
  /**
   * is null
   */
  ISNULL("is null"),
  /**
   * is not null
   */
  ISNOTNULL("is not null"),
  /**
   * and
   */
  AND("and"),
  /**
   * between
   */
  BETWEEN("between"),
  /**
   * or
   */
  OR("or");

  String code;

  OperatorEnum(String code) {
    this.code = code;
  }

}
