package com.yonyou.einvoice.common.metadata.mp;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User: andyxu Date: 2018/11/22 Time: 16:36
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface One {

  /**
   * id property name
   **/
  String idProperty() default "id";

  /**
   * id column name
   */
  String idColumn() default "";

}
