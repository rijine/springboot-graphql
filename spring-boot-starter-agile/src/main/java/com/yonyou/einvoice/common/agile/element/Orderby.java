package com.yonyou.einvoice.common.agile.element;

import com.yonyou.einvoice.common.agile.visitor.IMetaElement;
import com.yonyou.einvoice.common.agile.visitor.IVisitor;
import io.leangen.graphql.annotations.GraphQLNonNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

/**
 * order by子句。
 */
@Setter
@Getter
public class Orderby implements IMetaElement {

  @GraphQLNonNull
  private List<Orderbyitem> orderbyitems = new ArrayList<>();

  @Override
  public void accept(IVisitor visitor) {
    if (CollectionUtils.isEmpty(orderbyitems)) {
      return;
    }
    orderbyitems.forEach(orderbyitem -> {
      visitor.visit(orderbyitem);
    });
  }
}
