package com.yonyou.einvoice.common.metadata.element;

import com.yonyou.einvoice.common.metadata.visitor.IMetaElement;
import io.leangen.graphql.annotations.GraphQLNonNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 每个groupby项。和Field类型相似
 */
@Setter
@Getter
public class Groupbyitem implements IMetaElement {

  /**
   * group by的源数据库表别名
   */
  @GraphQLNonNull
  private String sourceAlias;
  /**
   * group by的源数据库表字段
   */
  @GraphQLNonNull
  private String field;
  private Aggr aggr;
  private String expr;
}
