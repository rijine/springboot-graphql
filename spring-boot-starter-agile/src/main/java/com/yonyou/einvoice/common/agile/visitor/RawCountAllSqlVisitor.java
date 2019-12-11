package com.yonyou.einvoice.common.agile.visitor;

import com.yonyou.einvoice.common.agile.element.EntityCondition;
import com.yonyou.einvoice.common.agile.element.Fields;
import com.yonyou.einvoice.common.agile.element.Limit;
import com.yonyou.einvoice.common.agile.element.Source;

/**
 * 用于生成可直接执行的sql。（获取符合条件的分页总数）
 *
 * @author liuqiangm
 */
public class RawCountAllSqlVisitor extends BaseSqlVisitor {

  private boolean outMostSource = true;

  @Override
  public void visit(EntityCondition sourceCondition) {
    Limit limit = sourceCondition.getLimit();
    /**
     * 对于分页查询的查询总数，无需设置Limit。
     */
    sourceCondition.setLimit(null);
    super.visit(sourceCondition);
    /**
     * 构造完sql之后，需要将limit设置回去
     */
    sourceCondition.setLimit(limit);
  }

  @Override
  public void visit(Source source) {
    if (outMostSource) {
      outMostSource = false;
      append("select count(1)");
      Fields fields = source.getFields();
      Limit limit = source.getLimit();
      source.setFields(null);
      source.setLimit(null);
      source.accept(this);
      source.setFields(fields);
      source.setLimit(limit);
      return;
    }
    super.visit(source);
  }

}
