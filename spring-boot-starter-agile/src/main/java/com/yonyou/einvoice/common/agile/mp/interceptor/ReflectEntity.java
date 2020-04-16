package com.yonyou.einvoice.common.agile.mp.interceptor;

import java.util.function.BiConsumer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
class ReflectEntity {

  /**
   * 数据库表的column名称
   */
  private String columnName;

  /**
   * 实体的property名称
   */
  private String propertyName;

  /**
   * 实体的property的type类型
   */
  private Class propertyType;

  /**
   * 将column的值赋值会propertyName属性的函数式接口 1) 需要执行属性赋值的对象 2) 需要赋的值
   */
  private BiConsumer<Object, Object> propertyValueConsumer;

}
