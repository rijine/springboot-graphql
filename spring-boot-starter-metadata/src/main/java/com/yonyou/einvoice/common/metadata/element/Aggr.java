package com.yonyou.einvoice.common.metadata.element;

import com.yonyou.einvoice.common.metadata.enums.AggrEnum;
import com.yonyou.einvoice.common.metadata.visitor.IMetaElement;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.types.GraphQLType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@GraphQLType(description = "用于描述sql中的聚合函数如：count或sum")
public class Aggr implements IMetaElement {

  @GraphQLNonNull
  @GraphQLQuery(description = "聚合函数类型：count或sum")
  private AggrEnum aggrEnum;
  @GraphQLQuery(description = "聚合函数内部的取值是否唯一。如：count(distinct id)")
  private Boolean distinct = false;
}
