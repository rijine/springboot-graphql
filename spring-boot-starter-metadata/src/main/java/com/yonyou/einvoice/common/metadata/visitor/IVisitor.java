package com.yonyou.einvoice.common.metadata.visitor;

import com.yonyou.einvoice.common.metadata.element.Condition;
import com.yonyou.einvoice.common.metadata.element.Conditions;
import com.yonyou.einvoice.common.metadata.element.Entity;
import com.yonyou.einvoice.common.metadata.element.EntityCondition;
import com.yonyou.einvoice.common.metadata.element.Field;
import com.yonyou.einvoice.common.metadata.element.Fields;
import com.yonyou.einvoice.common.metadata.element.Groupby;
import com.yonyou.einvoice.common.metadata.element.Groupbyitem;
import com.yonyou.einvoice.common.metadata.element.Having;
import com.yonyou.einvoice.common.metadata.element.Join;
import com.yonyou.einvoice.common.metadata.element.Limit;
import com.yonyou.einvoice.common.metadata.element.On;
import com.yonyou.einvoice.common.metadata.element.Orderby;
import com.yonyou.einvoice.common.metadata.element.Orderbyitem;
import com.yonyou.einvoice.common.metadata.element.Source;
import com.yonyou.einvoice.common.metadata.element.Value;

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
