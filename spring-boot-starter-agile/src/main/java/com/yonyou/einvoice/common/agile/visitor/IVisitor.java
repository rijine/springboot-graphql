package com.yonyou.einvoice.common.agile.visitor;

import com.yonyou.einvoice.common.agile.element.Condition;
import com.yonyou.einvoice.common.agile.element.Conditions;
import com.yonyou.einvoice.common.agile.element.Entity;
import com.yonyou.einvoice.common.agile.element.EntityCondition;
import com.yonyou.einvoice.common.agile.element.Field;
import com.yonyou.einvoice.common.agile.element.Fields;
import com.yonyou.einvoice.common.agile.element.Groupby;
import com.yonyou.einvoice.common.agile.element.Groupbyitem;
import com.yonyou.einvoice.common.agile.element.Having;
import com.yonyou.einvoice.common.agile.element.Join;
import com.yonyou.einvoice.common.agile.element.Limit;
import com.yonyou.einvoice.common.agile.element.On;
import com.yonyou.einvoice.common.agile.element.Orderby;
import com.yonyou.einvoice.common.agile.element.Orderbyitem;
import com.yonyou.einvoice.common.agile.element.Source;
import com.yonyou.einvoice.common.agile.element.Value;

/**
 * 访问者接口
 *
 * @author liuqiangm
 */
public interface IVisitor {

  void visit(Conditions conditions);

  void visit(Condition condition);

  void visit(Entity entity);

  void visit(Fields field);

  void visit(Field field);

  void visit(Groupby groupby);

  void visit(Groupbyitem groupbyitem);

  void visit(Having having);

  void visit(Join join);

  void visit(Limit limit);

  void visit(On on);

  void visit(Orderby orderby);

  void visit(Orderbyitem orderbyitem);

  void visit(Value value);

  void visit(Source source);

  void visit(EntityCondition sourceCondition);
}
