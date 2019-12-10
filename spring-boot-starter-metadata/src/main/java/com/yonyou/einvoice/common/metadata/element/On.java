package com.yonyou.einvoice.common.metadata.element;

import com.yonyou.einvoice.common.metadata.visitor.IMetaElement;
import io.leangen.graphql.annotations.GraphQLNonNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 表关联字段条件
 */
@Setter
@Getter
public class On implements IMetaElement {

  /**
   * 该字段使用源数据库表的表别名
   */
  @GraphQLNonNull
  String sourceAlias1;
  /**
   * 源数据库表的表字段
   */
  @GraphQLNonNull
  String field1;
  /**
   * 目标数据库表的表别名
   */
  @GraphQLNonNull
  String sourceAlias2;
  /**
   * 目标数据库表的表字段
   */
  @GraphQLNonNull
  String field2;

}
