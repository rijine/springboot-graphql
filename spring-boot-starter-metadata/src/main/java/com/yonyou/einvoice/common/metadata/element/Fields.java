package com.yonyou.einvoice.common.metadata.element;

import com.yonyou.einvoice.common.metadata.visitor.IMetaElement;
import com.yonyou.einvoice.common.metadata.visitor.IVisitor;
import io.leangen.graphql.annotations.GraphQLNonNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

/**
 * select后的所有field，聚合为Fields
 */
@Setter
@Getter
public class Fields implements IMetaElement {

  /**
   * select后面的所有字段，是否需要添加distinct 默认不加distinct
   */
  private Boolean distinct = false;

  /**
   * select的字段列表
   */
  @GraphQLNonNull
  private List<Field> fieldList;

  @Override
  public void accept(IVisitor visitor) {
    if (CollectionUtils.isEmpty(fieldList)) {
      throw new RuntimeException("select字段集合不能为空");
    }
    fieldList.forEach(field -> visitor.visit(field));
  }
}
