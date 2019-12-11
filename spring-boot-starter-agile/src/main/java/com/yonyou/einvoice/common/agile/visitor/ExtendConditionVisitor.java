package com.yonyou.einvoice.common.agile.visitor;

import com.yonyou.einvoice.common.agile.element.EntityCondition;
import com.yonyou.einvoice.common.agile.element.Field;
import com.yonyou.einvoice.common.agile.element.Groupbyitem;
import com.yonyou.einvoice.common.agile.element.On;
import com.yonyou.einvoice.common.agile.element.Orderbyitem;
import java.util.Set;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

@Setter
public class ExtendConditionVisitor extends BaseSqlVisitor {

  private Set<String> parentColumnSet;
  private Set<String> selfColumnSet;

  @Override
  public void visit(EntityCondition sourceCondition) {
    // 判断是否是扩展表。如果没有扩展，则不需要进行这个处理了。
    if (CollectionUtils.isEmpty(parentColumnSet) || CollectionUtils.isEmpty(selfColumnSet)) {
      return;
    }
    super.visit(sourceCondition);
  }

  @Override
  public void visit(Field field) {
    if (!"t0".equals(field.getSourceAlias())) {
      return;
    }
    field.setSourceAlias(getSourceAlias(field.getSourceAlias(), field.getField()));
    super.visit(field);
  }

  @Override
  public void visit(On on) {
    if (!"t0".equals(on.getSourceAlias1()) && !"t0".equals(on.getSourceAlias2())) {
      return;
    }
    on.setSourceAlias1(getSourceAlias(on.getSourceAlias1(), on.getField1()));
    on.setSourceAlias2(getSourceAlias(on.getSourceAlias2(), on.getField2()));
    super.visit(on);
  }

  @Override
  public void visit(Groupbyitem groupbyitem) {
    groupbyitem
        .setSourceAlias(getSourceAlias(groupbyitem.getSourceAlias(), groupbyitem.getField()));
    super.visit(groupbyitem);
  }

  @Override
  public void visit(Orderbyitem orderbyitem) {
    orderbyitem
        .setSourceAlias(getSourceAlias(orderbyitem.getSourceAlias(), orderbyitem.getField()));
    super.visit(orderbyitem);
  }

  private String getSourceAlias(String sourceAlias, String field) {
    if (!"t0".equals(sourceAlias)) {
      return sourceAlias;
    }
    // 判断当前字段是父表字段还是子表字段，此处进行修正
    if (parentColumnSet.contains(field)) {
      return "t_s0";
    }
    if (selfColumnSet.contains(field)) {
      return "t_s1";
    }
    return sourceAlias;
  }
}
