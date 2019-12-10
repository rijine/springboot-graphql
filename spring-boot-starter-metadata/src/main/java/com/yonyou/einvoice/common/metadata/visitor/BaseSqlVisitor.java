package com.yonyou.einvoice.common.metadata.visitor;

import com.google.common.base.CaseFormat;
import com.yonyou.einvoice.common.metadata.element.Condition;
import com.yonyou.einvoice.common.metadata.element.Conditions;
import com.yonyou.einvoice.common.metadata.element.Entity;
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
import com.yonyou.einvoice.common.metadata.enums.OperatorEnum;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * 基本的sql访问者。提供了生成sql时，rawVisitor和mybatisVisitor的公共方法
 *
 * @author liuqiangm
 */
public class BaseSqlVisitor extends BaseVisitor {

  protected StringBuilder sqlBuilder = new StringBuilder(256);

  @Override
  public void visit(Conditions conditions) {
    if (conditions == null || (CollectionUtils.isEmpty(conditions.getConditionList())
        && CollectionUtils
        .isEmpty(conditions.getAuthConditions()))) {
      return;
    }
    append("where");
    /**
     * 如果添加了默认的authConditions，则其应该放置于where语句的顶层。
     */
    if (!CollectionUtils.isEmpty(conditions.getAuthConditions())) {
      conditionsJoin(conditions.getAuthConditions(), OperatorEnum.AND);
      if (!CollectionUtils.isEmpty(conditions.getConditionList())) {
        append("and");
        append("(");
        conditionsJoin(conditions.getConditionList(), OperatorEnum.AND);
        append(")");
      }
      return;
    }
    conditionsJoin(conditions.getConditionList(), OperatorEnum.AND);
  }

  protected void processCondition(Condition condition) {
    // 如果存在exists子句
    if (condition.getExists() != null) {
      append("exists(");
      this.visit(condition.getExists());
      append(")");
      return;
    }

  }

  protected void processAndOrCondition(Condition condition) {
    OperatorEnum operatorEnum = condition.getOperator();
    if (operatorEnum == null) {
      throw new RuntimeException("condition的operator不能为空");
    }
    // and、or操作
    boolean andOrOperator =
        OperatorEnum.AND.equals(operatorEnum) || OperatorEnum.OR.equals(operatorEnum);
    if (!Objects.equals(andOrOperator, !CollectionUtils.isEmpty(condition.getConditionList()))) {
      throw new RuntimeException("and、or和list子集必须同时使用或同时不使用");
    }
    if (andOrOperator) {
      append("(");
      conditionsJoin(condition.getConditionList(), operatorEnum);
      append(")");
      return;
    }
    this.visit(condition.getSourceField());
  }

  @Override
  public void visit(Entity entity) {
    append("from").append(entity.getSource());
    if (!StringUtils.isEmpty(entity.getAlias())) {
      append(entity.getAlias());
    }
    super.visit(entity);
  }


  @Override
  public void visit(Fields fields) {
    append("select");
    if (Boolean.TRUE.equals(fields.getDistinct())) {
      append("distinct");
    }
    if (CollectionUtils.isEmpty(fields.getFieldList())) {
      throw new RuntimeException("select的字段集合不能为空");
    }
    List<Field> fieldList = fields.getFieldList();
    final int size = fieldList.size();
    for (int i = 0; i < size; i++) {
      Field field = fieldList.get(i);
      this.visit(field);
      if (i != size - 1) {
        sqlBuilder.setLength(sqlBuilder.length() - 1);
        append(",");
      }
    }
  }

  @Override
  public void visit(Field field) {
    boolean aggr = field.getAggr() != null;
    if (aggr) {
      if (field.getAggr().getAggrEnum() == null) {
        throw new RuntimeException("聚合函数的函数名不能为空");
      }
      sqlBuilder.append(field.getAggr().getAggrEnum().getCode()).append("(");
      if (Boolean.TRUE.equals(field.getAggr().getDistinct())) {
        append("distinct");
      }
    }
    String var = String.format("`%s`",
        CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getField()));
    if (!StringUtils.isEmpty(field.getSourceAlias())) {
      var = String.format("%s.%s", field.getSourceAlias(), var);
    }
    // 判断field是否存在复杂的表达式
    if (!StringUtils.isEmpty(field.getExpr())) {
      var = field.getExpr().replaceAll("\\$\\{var\\}", var);
    }
    append(var);
    if (aggr) {
      sqlBuilder.setLength(sqlBuilder.length() - 1);
      append(")");
    }
    if (field.getAlias() != null) {
      append("as").append(field.getAlias());
    }
    super.visit(field);
  }

  @Override
  public void visit(Groupby groupby) {
    append("group by");
    super.visit(groupby);
    removeTail();
  }

  @Override
  public void visit(Groupbyitem groupbyitem) {
    if (groupbyitem.getAggr() != null && groupbyitem.getAggr().getAggrEnum() == null) {
      throw new RuntimeException("group by的聚合字段必须包含聚合函数类型");
    }
    String var = String.format("`%s`",
        CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, groupbyitem.getField()));
    if (!StringUtils.isEmpty(groupbyitem.getSourceAlias())) {
      var = String.format("%s.%s", groupbyitem.getSourceAlias(), var);
    }
    // 判断field是否存在复杂的表达式
    if (!StringUtils.isEmpty(groupbyitem.getExpr())) {
      var = groupbyitem.getExpr().replaceAll("\\$\\{var\\}", var);
    }
    if (groupbyitem.getAggr() != null) {
      append(String.format("%s(%s%s),", groupbyitem.getAggr().getAggrEnum().getCode(),
          groupbyitem.getAggr().getDistinct() ? "distinct " : "", var));
      return;
    }
    append(String.format("%s,", CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, var)));
  }

  @Override
  public void visit(Having having) {
    if (having == null || CollectionUtils.isEmpty(having.getConditionList())) {
      return;
    }
    append("having");
    Iterator<Condition> iterator = having.getConditionList().iterator();
    Condition condition = iterator.next();
    this.visit(condition);
    while (iterator.hasNext()) {
      condition = iterator.next();
      append("and");
      this.visit(condition);
    }
  }

  @Override
  public void visit(Join join) {
    if (Objects.equals(join.getTargetSource() != null, join.getTarget() != null)) {
      throw new RuntimeException("targetSource和target不能同时为空或同时非空");
    }
    if (StringUtils.isEmpty(join.getAlias())) {
      throw new RuntimeException("使用join语句必须指定alias");
    }
    if (join.getTargetSource() != null) {
      append(join.getJointype().getCode()).append("(");
      this.visit(join.getTargetSource());
      append(join.getAlias());
      this.visit(join.getOn());
      return;
    }
    if (join.getTarget() != null) {
      append(join.getJointype().getCode()).append(join.getTarget())
          .append(join.getAlias());
      this.visit(join.getOn());
      return;
    }
  }


  @Override
  public void visit(Limit limit) {
    if (limit.getPageIndex() != null) {
      if (limit.getSize() == null) {
        throw new RuntimeException("使用limit语句必须指定size和（pageIndex或offset）");
      }
      limit.setOffset((limit.getPageIndex() - 1) * limit.getSize());
    }
    append(String.format("limit %d offset %d", limit.getSize(), limit.getOffset()));
  }


  @Override
  public void visit(On on) {
    if (StringUtils.isEmpty(on.getSourceAlias1()) || StringUtils.isEmpty(on.getSourceAlias2())) {
      throw new RuntimeException("使用on语句必须指定sourceAlias1和sourceAlias2");
    }
    String field1 = String
        .format("`%s`", CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, on.getField1()));
    String field2 = String
        .format("`%s`",
            CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, on.getField2()));
    append("on").append(String
        .format("%s.%s = %s.%s", on.getSourceAlias1(), field1,
            on.getSourceAlias2(), field2));
  }

  @Override
  public void visit(Orderby orderby) {
    append("order by");
    super.visit(orderby);
    removeTail();
  }

  @Override
  public void visit(Orderbyitem orderbyitem) {
    if (orderbyitem.getAggr() != null && orderbyitem.getAggr().getAggrEnum() == null) {
      throw new RuntimeException("order by中使用聚合函数，则聚合函数名称不能为空");
    }
    if (orderbyitem.getFieldAlias() != null) {
      append(String
          .format("%s %s,", orderbyitem.getFieldAlias(), orderbyitem.getDirection().getCode()));
      return;
    }
    String var = String
        .format("`%s`",
            CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, orderbyitem.getField()));
    if (!StringUtils.isEmpty(orderbyitem.getSourceAlias())) {
      var = String.format("%s.%s", orderbyitem.getSourceAlias(), var);
    }
    if (orderbyitem.getAggr() != null) {
      append(String.format("%s(%s%s) %s,", orderbyitem.getAggr().getAggrEnum().getCode(),
          orderbyitem.getAggr().getDistinct() ? "distinct " : "", var,
          orderbyitem.getDirection().getCode()));
      return;
    }
    append(String.format("%s %s,", var,
        orderbyitem.getDirection().getCode()));
  }

  @Override
  public void visit(Source source) {
    super.visit(source);
  }


  private void removeTail() {
    /**
     * 删除groupbyitem或orderbyitem的最后一个','
     */
    sqlBuilder.setLength(sqlBuilder.length() - 2);
    sqlBuilder.append(" ");
  }


  protected void conditionsJoin(List<Condition> conditionList,
      OperatorEnum conditionLogicEnum) {
    if (CollectionUtils.isEmpty(conditionList)) {
      return;
    }
    if (!OperatorEnum.AND.equals(conditionLogicEnum) && !OperatorEnum.OR
        .equals(conditionLogicEnum)) {
      throw new RuntimeException("多个condition连接，只能使用and或or操作符");
    }
    final int size = conditionList.size();
    for (int i = 0; i < size; i++) {
      this.visit(conditionList.get(i));
      if (i != size - 1) {
        append(conditionLogicEnum.getCode());
      }
    }
  }

  protected BaseSqlVisitor append(String str) {
    sqlBuilder.append(str).append(" ");
    return this;
  }

  public void reset() {
    sqlBuilder.setLength(0);
  }

  public String getSql() {
    int length = sqlBuilder.length();
    if (length == 0) {
      return "";
    }
    sqlBuilder.setLength(length - 1);
    String result = sqlBuilder.toString();
    sqlBuilder.setLength(length);
    return result;
  }

}
