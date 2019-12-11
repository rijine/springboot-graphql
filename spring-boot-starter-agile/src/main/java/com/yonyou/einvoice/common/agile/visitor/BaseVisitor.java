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
 * 基本的访问者实现类。用于提供默认的访问策略
 *
 * @author liuqiangm
 */
public class BaseVisitor implements IVisitor {

  @Override
  public void visit(Conditions conditions) {
    if (conditions == null) {
      return;
    }
    conditions.accept(this);
  }

  @Override
  public void visit(Condition condition) {
    if (condition == null) {
      return;
    }
    condition.accept(this);
  }

  @Override
  public void visit(Entity entity) {
    if (entity == null) {
      return;
    }
    entity.accept(this);
  }

  @Override
  public void visit(Fields field) {
    if (field == null) {
      return;
    }
    field.accept(this);
  }

  @Override
  public void visit(Field field) {
    if (field == null) {
      return;
    }
    field.accept(this);
  }

  @Override
  public void visit(Groupby groupby) {
    if (groupby == null) {
      return;
    }
    groupby.accept(this);
  }

  @Override
  public void visit(Groupbyitem groupbyitem) {
    if (groupbyitem == null) {
      return;
    }
    groupbyitem.accept(this);
  }

  @Override
  public void visit(Having having) {
    if (having == null) {
      return;
    }
    having.accept(this);
  }

  @Override
  public void visit(Join join) {
    if (join == null) {
      return;
    }
    join.accept(this);
  }

  @Override
  public void visit(Limit limit) {
    if (limit == null) {
      return;
    }
    limit.accept(this);
  }

  @Override
  public void visit(On on) {
    if (on == null) {
      return;
    }
    on.accept(this);
  }

  @Override
  public void visit(Orderby orderby) {
    if (orderby == null) {
      return;
    }
    orderby.accept(this);
  }

  @Override
  public void visit(Orderbyitem orderbyitem) {
    if (orderbyitem == null) {
      return;
    }
    orderbyitem.accept(this);
  }

  @Override
  public void visit(Value value) {
    if (value == null) {
      return;
    }
    value.accept(this);
  }

  @Override
  public void visit(Source source) {
    if (source == null) {
      return;
    }
    source.accept(this);
  }

  @Override
  public void visit(EntityCondition sourceCondition) {
    if (sourceCondition == null) {
      return;
    }
    sourceCondition.accept(this);
  }
}
