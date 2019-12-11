package com.yonyou.einvoice.common.metadata.element;

import com.yonyou.einvoice.common.metadata.enums.DirectionEnum;
import com.yonyou.einvoice.common.metadata.enums.JointypeEnum;
import com.yonyou.einvoice.common.metadata.enums.OperatorEnum;
import com.yonyou.einvoice.common.metadata.visitor.IMetaElement;
import com.yonyou.einvoice.common.metadata.visitor.IVisitor;
import io.leangen.graphql.annotations.GraphQLIgnore;
import io.leangen.graphql.annotations.types.GraphQLType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * 作为select * from table t0 后面所附加的动态查询条件拼接。
 *
 * @author liuqiangm
 */
@Setter
@Getter
@GraphQLType
public class EntityCondition implements IMetaElement {

  /**
   * 作为查询条件拼接到mapper的sql标签后面时，该字段用于判断select后面的字段是否整体需要添加distinct关键字。 默认不加
   */
  private Boolean distinct = false;
  /**
   * 表连接条件
   */
  private List<Join> joins;
  /**
   * 查询条件
   */
  private Conditions conditions;
  /**
   * group by 语句
   */
  private Groupby groupby;
  /**
   * having 语句
   */
  private Having having;
  /**
   * order by 语句
   */
  private Orderby orderby;
  /**
   * limit 语句
   */
  private Limit limit;

  @Override
  public void accept(IVisitor visitor) {
    if (!CollectionUtils.isEmpty(joins)) {
      for (Join join : joins) {
        visitor.visit(join);
      }
    }
    if (conditions != null) {
      visitor.visit(conditions);
    }
    if (groupby != null) {
      visitor.visit(groupby);
    }
    if (having != null) {
      visitor.visit(having);
    }
    if (orderby != null) {
      visitor.visit(orderby);
    }
    if (limit != null) {
      visitor.visit(limit);
    }
  }

  @GraphQLIgnore
  public static EntityConditionBuilder builder() {
    return new EntityConditionBuilder();
  }

  public static class EntityConditionBuilder {

    private Boolean distinct = false;
    private List<Join> joins = new ArrayList<>();
    private Conditions conditions;
    private Groupby groupby;
    private Having having;
    private Orderby orderby;
    private Limit limit;

    private Join tmpJoin;
    private On tmpOn;

    private Condition tmpCondition = null;

    private Stack<Condition> preConditionStack = new Stack<>();

    private Stack<OperatorEnum> operatorEnumStack = new Stack<>();

    private Condition tmpHavingCondition;

    private EntityConditionBuilder() {
    }

    public EntityConditionBuilder distinct(Boolean distinct) {
      this.distinct = distinct;
      return this;
    }

    public EntityConditionBuilder innerJoin(String target, String alias) {
      tmpJoin = new Join();
      tmpJoin.setJointype(JointypeEnum.INNERJOIN);
      tmpJoin.setTarget(target);
      tmpJoin.setAlias(alias);
      return this;
    }

    public EntityConditionBuilder leftJoin(String target, String alias) {
      tmpJoin = new Join();
      tmpJoin.setJointype(JointypeEnum.LEFTJOIN);
      tmpJoin.setTarget(target);
      tmpJoin.setAlias(alias);
      return this;
    }

    public EntityConditionBuilder rightJoin(String target, String alias) {
      tmpJoin = new Join();
      tmpJoin.setJointype(JointypeEnum.RIGHTJOIN);
      tmpJoin.setTarget(target);
      tmpJoin.setAlias(alias);
      return this;
    }

    public EntityConditionBuilder on(String sourceAlias1, String field1, String sourceAlias2,
        String field2) {
      tmpOn = new On();
      tmpOn.setSourceAlias1(sourceAlias1);
      tmpOn.setField1(field1);
      tmpOn.setSourceAlias2(sourceAlias2);
      tmpOn.setField2(field2);
      tmpJoin.setOn(tmpOn);
      joins.add(tmpJoin);
      return this;
    }

    /**
     * 表明当前开始一个where语句
     *
     * @return
     */
    public EntityConditionBuilder where() {
      conditions = new Conditions();
      operatorEnumStack.push(OperatorEnum.AND);
      return this;
    }

    public EntityConditionBuilder andStart() {
      addCondition();
      operatorEnumStack.push(OperatorEnum.AND);
      tmpCondition = new Condition();
      tmpCondition.setOperator(OperatorEnum.AND);
      preConditionStack.push(tmpCondition);
      tmpCondition = null;
      return this;
    }

    public EntityConditionBuilder andEnd() {
      addCondition();
      operatorEnumStack.pop();
      tmpCondition = preConditionStack.pop();
      return this;
    }

    public EntityConditionBuilder orStart() {
      addCondition();
      operatorEnumStack.push(OperatorEnum.OR);
      tmpCondition = new Condition();
      tmpCondition.setOperator(OperatorEnum.OR);
      preConditionStack.push(tmpCondition);
      tmpCondition = null;
      return this;
    }

    public EntityConditionBuilder orEnd() {
      addCondition();
      operatorEnumStack.pop();
      tmpCondition = preConditionStack.pop();
      return this;
    }

    /**
     * 拼接where条件的操作符前面部分。例如：t0.a != 1，则sourceAlias为t0，sourceField为a
     *
     * @param sourceAlias
     * @param sourceField
     * @return
     */
    public EntityConditionBuilder field(String sourceAlias, String sourceField) {
      addCondition();
      tmpCondition = new Condition();
      Field conditionField = new Field();
      conditionField.setSourceAlias(sourceAlias);
      conditionField.setField(sourceField);
      tmpCondition.setSourceField(conditionField);
      return this;
    }

    /**
     * condition条件判断类型
     *
     * @param operatorEnum
     * @return
     */
    private EntityConditionBuilder operator(OperatorEnum operatorEnum) {
      tmpCondition.setOperator(operatorEnum);
      return this;
    }

    public EntityConditionBuilder eq(String strValue) {
      tmpCondition.setOperator(OperatorEnum.EQUAL);
      this.strValue(strValue);
      return this;
    }

    public EntityConditionBuilder eq(String sourceAlias, String sourceField) {
      tmpCondition.setOperator(OperatorEnum.EQUAL);
      this.fieldValue(sourceAlias, sourceField);
      return this;
    }

    public EntityConditionBuilder notEq(String strValue) {
      tmpCondition.setOperator(OperatorEnum.NOTEQUAL);
      this.strValue(strValue);
      return this;
    }

    public EntityConditionBuilder notEq(String sourceAlias, String sourceField) {
      tmpCondition.setOperator(OperatorEnum.NOTEQUAL);
      this.fieldValue(sourceAlias, sourceField);
      return this;
    }

    public EntityConditionBuilder greater(String strValue) {
      tmpCondition.setOperator(OperatorEnum.GREATER);
      this.strValue(strValue);
      return this;
    }

    public EntityConditionBuilder greater(String sourceAlias, String sourceField) {
      tmpCondition.setOperator(OperatorEnum.GREATER);
      this.fieldValue(sourceAlias, sourceField);
      return this;
    }

    public EntityConditionBuilder less(String strValue) {
      tmpCondition.setOperator(OperatorEnum.LESS);
      this.strValue(strValue);
      return this;
    }

    public EntityConditionBuilder less(String sourceAlias, String sourceField) {
      tmpCondition.setOperator(OperatorEnum.LESS);
      this.fieldValue(sourceAlias, sourceField);
      return this;
    }

    public EntityConditionBuilder greaterEqual(String strValue) {
      tmpCondition.setOperator(OperatorEnum.GREATEREQUAL);
      this.strValue(strValue);
      return this;
    }

    public EntityConditionBuilder greaterEqual(String sourceAlias, String sourceField) {
      tmpCondition.setOperator(OperatorEnum.GREATEREQUAL);
      this.fieldValue(sourceAlias, sourceField);
      return this;
    }

    public EntityConditionBuilder lessEqual(String strValue) {
      tmpCondition.setOperator(OperatorEnum.LESSEQUAL);
      this.strValue(strValue);
      return this;
    }

    public EntityConditionBuilder lessEqual(String sourceAlias, String sourceField) {
      tmpCondition.setOperator(OperatorEnum.LESSEQUAL);
      this.fieldValue(sourceAlias, sourceField);
      return this;
    }

    public EntityConditionBuilder like(String strValue) {
      tmpCondition.setOperator(OperatorEnum.LIKE);
      if (StringUtils.isEmpty(strValue)) {
        this.strValue("%%");
        return this;
      }
      this.strValue("%" + strValue + "%");
      return this;
    }

    public EntityConditionBuilder likeStart(String strValue) {
      if (StringUtils.isEmpty(strValue)) {
        this.strValue("%%");
        return this;
      }
      tmpCondition.setOperator(OperatorEnum.LIKE);
      this.strValue(strValue + "%");
      return this;
    }

    public EntityConditionBuilder likeEnd(String strValue) {
      if (StringUtils.isEmpty(strValue)) {
        this.strValue("%%");
        return this;
      }
      tmpCondition.setOperator(OperatorEnum.LIKE);
      this.strValue("%" + strValue);
      return this;
    }

    public EntityConditionBuilder in(List list) {
      tmpCondition.setOperator(OperatorEnum.IN);
      this.listValue(list);
      return this;
    }

    public EntityConditionBuilder in(Collection list) {
      tmpCondition.setOperator(OperatorEnum.IN);
      if (list == null) {
        this.listValue(null);
        return this;
      }
      this.listValue(Arrays.asList(list.toArray()));
      return this;
    }

    public EntityConditionBuilder notIn(List list) {
      tmpCondition.setOperator(OperatorEnum.NOTIN);
      this.listValue(list);
      return this;
    }

    public EntityConditionBuilder notIn(Collection list) {
      tmpCondition.setOperator(OperatorEnum.NOTIN);
      if (list == null) {
        this.listValue(null);
        return this;
      }
      this.listValue(Arrays.asList(list.toArray()));
      return this;
    }

    public EntityConditionBuilder isNull() {
      tmpCondition.setOperator(OperatorEnum.ISNULL);
      return this;
    }

    public EntityConditionBuilder isNotNull() {
      tmpCondition.setOperator(OperatorEnum.ISNOTNULL);
      return this;
    }

    public EntityConditionBuilder between(String strValue1, String strValue2) {
      tmpCondition.setOperator(OperatorEnum.BETWEEN);
      this.strValue(strValue1, strValue2);
      return this;
    }

    /**
     * in、not in语句等使用
     *
     * @param list
     * @return
     */
    private EntityConditionBuilder listValue(List list) {
      Value val = new Value();
      val.setList(list);
      tmpCondition.setV1(val);
      return this;
    }

    /**
     * 用于where语句中添加表连接查询。如：where t0.id = t1.hid
     *
     * @param sourceAlias
     * @param sourceField
     * @return
     */
    private EntityConditionBuilder fieldValue(String sourceAlias, String sourceField) {
      Field field = new Field();
      field.setSourceAlias(sourceAlias);
      field.setField(sourceField);
      Value value = new Value();
      value.setField(field);
      tmpCondition.setV1(value);
      return this;
    }

    /**
     * in一个子Source对象
     *
     * @param source
     * @return
     */
    private EntityConditionBuilder sourceValue(Source source) {
      Value val = new Value();
      val.setSource(source);
      tmpCondition.setV1(val);
      return this;
    }

    /**
     * v1赋值
     *
     * @param v1
     * @return
     */
    private EntityConditionBuilder strValue(String v1) {
      Value val = new Value();
      val.setVal(v1);
      tmpCondition.setV1(val);
      return this;
    }

    /**
     * v1、v2赋值，如between语句使用
     *
     * @param v1
     * @param v2
     * @return
     */
    private EntityConditionBuilder strValue(String v1, String v2) {
      Value val1 = new Value();
      Value val2 = new Value();
      val1.setVal(v1);
      val2.setVal(v2);
      tmpCondition.setV1(val1);
      tmpCondition.setV2(val2);
      return this;
    }

    /**
     * group by语句构造
     *
     * @param sourceAlias
     * @param field
     * @return
     */
    public EntityConditionBuilder groupby(String sourceAlias, String field) {
      if (groupby == null) {
        groupby = new Groupby();
      }
      Groupbyitem groupbyitem = new Groupbyitem();
      groupbyitem.setSourceAlias(sourceAlias);
      groupbyitem.setField(field);
      groupby.getGroupbyitems().add(groupbyitem);
      return this;
    }

    /**
     * 分页语句构造
     *
     * @param pageIndex
     * @param size
     * @return
     */
    public EntityConditionBuilder page(Integer pageIndex, Integer size) {
      limit = new Limit();
      limit.setPageIndex(pageIndex);
      limit.setSize(size);
      return this;
    }

    /**
     * limit语句
     *
     * @param offset
     * @param size
     * @return
     */
    public EntityConditionBuilder limit(Integer offset, Integer size) {
      limit = new Limit();
      limit.setOffset(offset);
      limit.setSize(size);
      return this;
    }

    public EntityConditionBuilder havingField(String sourceAlias, String sourceField) {
      if (having == null) {
        having = new Having();
      }
      tmpHavingCondition = new Condition();
      Field field = new Field();
      field.setSourceAlias(sourceAlias);
      field.setField(sourceField);
      tmpHavingCondition.setSourceField(field);
      having.getConditionList().add(tmpHavingCondition);
      return this;
    }

    public EntityConditionBuilder havingOperator(OperatorEnum operatorEnum) {
      tmpHavingCondition.setOperator(operatorEnum);
      return this;
    }

    public EntityConditionBuilder havingStrValue(String strValue) {
      Value value = new Value();
      value.setVal(strValue);
      tmpHavingCondition.setV1(value);
      return this;
    }

    public EntityConditionBuilder havingStrValue(String strValue1, String strValue2) {
      Value val1 = new Value();
      Value val2 = new Value();
      val1.setVal(strValue1);
      val2.setVal(strValue2);
      tmpHavingCondition.setV1(val1);
      tmpHavingCondition.setV2(val2);
      return this;
    }

    public EntityConditionBuilder havingListValue(String strValue) {
      Value value = new Value();
      value.setVal(strValue);
      tmpHavingCondition.setV1(value);
      return this;
    }

    /**
     * orderby语句
     */
    public EntityConditionBuilder orderbyAsc(String sourceAlias, String sourceField) {
      return orderbyAsc(sourceAlias, sourceField, DirectionEnum.ASC);
    }

    public EntityConditionBuilder orderByDesc(String sourceAlias, String sourceField) {
      return orderbyAsc(sourceAlias, sourceField, DirectionEnum.DESC);
    }

    /**
     * orderby语句，包含方向
     */
    private EntityConditionBuilder orderbyAsc(String sourceAlias, String field,
        DirectionEnum directionEnum) {
      if (orderby == null) {
        orderby = new Orderby();
      }
      Orderbyitem orderbyitem = new Orderbyitem();
      orderbyitem.setSourceAlias(sourceAlias);
      orderbyitem.setField(field);
      orderbyitem.setDirection(directionEnum);
      orderby.getOrderbyitems().add(orderbyitem);
      return this;
    }

    public EntityCondition build() {
      addCondition();
      EntityCondition sourceCondition = new EntityCondition();
      sourceCondition.setDistinct(this.distinct);
      sourceCondition.setConditions(this.conditions);
      sourceCondition.setGroupby(this.groupby);
      sourceCondition.setHaving(this.having);
      sourceCondition.setJoins(this.joins);
      sourceCondition.setLimit(this.limit);
      sourceCondition.setOrderby(this.orderby);
      return sourceCondition;
    }

    private void addCondition() {
      if (tmpCondition != null && preConditionStack.isEmpty()) {
        conditions.getConditionList().add(tmpCondition);
      } else if (tmpCondition != null) {
        preConditionStack.peek().getConditionList().add(tmpCondition);
      }
    }
  }
}
