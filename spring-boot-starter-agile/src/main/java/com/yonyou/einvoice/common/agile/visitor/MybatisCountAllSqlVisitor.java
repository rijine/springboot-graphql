package com.yonyou.einvoice.common.agile.visitor;

import com.yonyou.einvoice.common.agile.element.EntityCondition;
import com.yonyou.einvoice.common.agile.element.Fields;
import com.yonyou.einvoice.common.agile.element.Limit;
import com.yonyou.einvoice.common.agile.element.Source;

/**
 * 用于生成mybatis的分页查询总数sql
 *
 * @author liuqiangm
 */
public class MybatisCountAllSqlVisitor extends MybatisSqlVisitor {

  /**
   * 兼容了使用EntityCondition作为条件拼接和使用Source直接拼接的场景
   */
  private boolean outMostSource = true;


  @Override
  public void visit(Source source) {
    if (outMostSource) {
      outMostSource = false;
      append("select count(1)");
      Fields fields = source.getFields();
      Limit limit = source.getLimit();
      source.setFields(null);
      source.setLimit(null);
      super.visit(source);
      source.setFields(fields);
      source.setLimit(limit);
      return;
    }
    super.visit(source);
  }

  @Override
  public void visit(EntityCondition sourceCondition) {
    outMostSource = false;
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

}
