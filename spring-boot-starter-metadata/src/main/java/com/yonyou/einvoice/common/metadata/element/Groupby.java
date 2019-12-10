package com.yonyou.einvoice.common.metadata.element;

import com.yonyou.einvoice.common.metadata.visitor.IMetaElement;
import com.yonyou.einvoice.common.metadata.visitor.IVisitor;
import io.leangen.graphql.annotations.GraphQLNonNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

/**
 * group by整体，可能包含多个groupby子项
 *
 * @author liuqiangm
 */
@Setter
@Getter
public class Groupby implements IMetaElement {

  /**
   * group by的子项
   */
  @GraphQLNonNull
  List<Groupbyitem> groupbyitems;

  @Override
  public void accept(IVisitor visitor) {
    if (CollectionUtils.isEmpty(groupbyitems)) {
      throw new RuntimeException("group by子句不能为空");
    }
    groupbyitems.forEach(groupbyitem -> visitor.visit(groupbyitem));
  }
}
