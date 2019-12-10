package com.yonyou.einvoice.common.metadata.element;

import com.yonyou.einvoice.common.metadata.enums.JointypeEnum;
import com.yonyou.einvoice.common.metadata.visitor.IMetaElement;
import com.yonyou.einvoice.common.metadata.visitor.IVisitor;
import io.leangen.graphql.annotations.GraphQLNonNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 表连接条件 join可以包含两种不同的表连接： 1. join一个select语句，这种情况下需要对targetSource进行赋值。 2.
 * join一个数据库表名+别名，这种情况下需要对target+alias进行赋值。
 *
 * @author liuqiangm
 */
@Getter
@Setter
public class Join implements IMetaElement {

  private Source targetSource;
  private String target;
  private String alias;
  /**
   * 表连接类型。innerjoin/leftjoin/rightjoin等
   */
  @GraphQLNonNull
  private JointypeEnum jointype;
  /**
   * 表连接的字段
   */
  @GraphQLNonNull
  private On on;

  @Override
  public void accept(IVisitor visitor) {
    if (targetSource != null) {
      visitor.visit(targetSource);
    }
    if (on != null) {
      visitor.visit(on);
    }
  }
}
