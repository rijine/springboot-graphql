package com.yonyou.einvoice.common.metadata.element;

import com.yonyou.einvoice.common.metadata.visitor.IMetaElement;
import io.leangen.graphql.annotations.GraphQLNonNull;
import lombok.Getter;
import lombok.Setter;

/**
 * limit语句。 添加了分页属性设置。 只能设置pageIndex + size或offset + size
 */
@Setter
@Getter
public class Limit implements IMetaElement {

  @GraphQLNonNull
  private Integer size;
  @GraphQLNonNull
  private Integer offset;
  @GraphQLNonNull
  private Integer pageIndex;
}
