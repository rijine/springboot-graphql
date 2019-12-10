package com.yonyou.einvoice.common.metadata.element;

import com.yonyou.einvoice.common.metadata.visitor.IMetaElement;
import com.yonyou.einvoice.common.metadata.visitor.IVisitor;
import io.leangen.graphql.annotations.types.GraphQLType;
import lombok.Getter;
import lombok.Setter;

/**
 * source对象。可以理解为一条完整的select语句。
 *
 * @author liuqiangm
 */
@Setter
@Getter
@GraphQLType
public class Source implements IMetaElement {

  private Fields fields;
  private Entity entity;
  private Conditions conditions;
  private Groupby groupby;
  private Having having;
  private Orderby orderby;
  private Limit limit;

  @Override
  public void accept(IVisitor visitor) {
    if (fields != null) {
      visitor.visit(fields);
    }
    if (entity != null) {
      visitor.visit(entity);
    }
    if (conditions != null) {
      visitor.visit(conditions);
    }
    if (groupby != null) {
      visitor.visit(groupby);
    }
    if (having != null) {
      visitor.visit(having);
    }
    if (orderby != null) {
      visitor.visit(orderby);
    }
    if (limit != null) {
      visitor.visit(limit);
    }
  }
}
