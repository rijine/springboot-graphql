package com.yonyou.einvoice.common.metadata.element;

import com.yonyou.einvoice.common.metadata.visitor.IMetaElement;
import com.yonyou.einvoice.common.metadata.visitor.IVisitor;
import io.leangen.graphql.annotations.GraphQLNonNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

/**
 * 查询语句的实体类型
 *
 * @author liuqiangm
 */
@Setter
@Getter
public class Entity implements IMetaElement {

  /**
   * 查询的源数据库表
   */
  @GraphQLNonNull
  private String source;
  /**
   * 查询的源数据库表别名
   */
  @GraphQLNonNull
  private String alias;
  /**
   * join条件
   */
  private List<Join> joins;

  @Override
  public void accept(IVisitor visitor) {
    if (CollectionUtils.isEmpty(joins)) {
      return;
    }
    joins.forEach(join -> visitor.visit(join));
  }
}
