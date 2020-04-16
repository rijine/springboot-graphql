package com.yonyou.einvoice.common.agile.mp.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于配置某个字段为聚合字段 聚合字段指的是：该字段为一个对象，对象内包含许多其他的字段。
 *
 * @author liuqiangm
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AggField {

  /**
   * 聚合对象的字段前缀。需要搭配AggDetailIndex的aggIndex一起使用。 如：prefix为name，index为1，则映射到的数据库的字段为：name1
   *
   * @return
   */
  String aggPrefix() default "";
}
