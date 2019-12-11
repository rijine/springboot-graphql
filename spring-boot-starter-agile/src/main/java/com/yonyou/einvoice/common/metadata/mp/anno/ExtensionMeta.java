package com.yonyou.einvoice.common.metadata.mp.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用该注解注释的mapper类，表明该mapper类是否是扩展类mapper，扩展自那个实体类
 *
 * @author liuqiangm
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ExtensionMeta {

  Class<?> entityClazz();
}
