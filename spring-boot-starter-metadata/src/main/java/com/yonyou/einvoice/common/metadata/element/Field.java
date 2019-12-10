package com.yonyou.einvoice.common.metadata.element;

import com.yonyou.einvoice.common.metadata.visitor.IMetaElement;
import io.leangen.graphql.annotations.GraphQLNonNull;
import lombok.Getter;
import lombok.Setter;

/**
 * select语句的具体select字段
 *
 * @author liuqiangm
 */
@Setter
@Getter
public class Field implements IMetaElement {

  /***
   * 源数据库表的别名
   */
  @GraphQLNonNull
  private String sourceAlias;
  /**
   * 源数据库表的字段
   */
  @GraphQLNonNull
  private String field;
  /**
   * 查询的字段整体赋别名
   */
  @GraphQLNonNull
  private String alias;
  /**
   * 聚合函数
   */
  private Aggr aggr;
  /**
   * 其他表达式拼接
   */
  private String expr;
}
