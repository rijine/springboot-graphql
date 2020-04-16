package com.yonyou.einvoice.common.agile.mp.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用该注解注释的实体类字段，在批量插入时，会自动忽略。
 *
 * @author liuqiangm
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AggDetailIndex {

  String aggIndex();
}
