package com.yonyou.einvoice.common.agile.element;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.yonyou.einvoice.common.agile.element.EntityCondition.EntityConditionBuilder;
import com.yonyou.einvoice.common.agile.enums.OperatorEnum;
import java.util.Collection;
import java.util.List;

public class TwoEntityConditionBuilder<T, U> extends
    EntityConditionBuilder implements ITwoEntityBuilder<T, U> {

  private Class<T> tClass;
  private Class<U> uClass;

  protected TwoEntityConditionBuilder(Class<T> tClass, Class<U> uClass) {
    this.tClass = tClass;
    this.uClass = uClass;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> innerJoin(Class<U> uClass) {
    this.innerJoin(EntityCondition.getTableName(uClass), "t1");
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> leftJoin(Class<U> uClass) {
    this.leftJoin(EntityCondition.getTableName(uClass), "t1");
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> rightJoin(Class<U> uClass) {
    this.rightJoin(EntityCondition.getTableName(uClass), "t1");
    return this;
  }

  /**
   * 支持lambda表达式拼接on查询条件
   *
   * @param sFunction1
   * @param sFunction2
   * @return
   */
  @Override
  public TwoEntityConditionBuilder<T, U> on(SFunction<T, ?> sFunction1,
      SFunction<U, ?> sFunction2) {
    String column1 = EntityCondition.getColumnFromSFunction(sFunction1);
    String column2 = EntityCondition.getColumnFromSFunction(sFunction2);
    this.on("t0", column1, "t1", column2);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> field(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.field("t0", column);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> field(Ex1SFunction<U, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.field("t1", column);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> eq(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.eq("t0", column);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> eq(Ex1SFunction<U, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.eq("t1", column);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> notEq(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.notEq("t0", column);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> notEq(Ex1SFunction<U, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.notEq("t1", column);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> greater(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.greater("t0", column);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> greater(Ex1SFunction<U, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.greater("t1", column);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> less(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.less("t0", column);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> less(Ex1SFunction<U, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.less("t1", column);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> greaterEqual(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.greaterEqual("t0", column);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> greaterEqual(Ex1SFunction<U, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.greaterEqual("t1", column);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> lessEqual(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.lessEqual("t0", column);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> lessEqual(Ex1SFunction<U, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.lessEqual("t1", column);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> groupby(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.groupby("t0", column);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> groupby(Ex1SFunction<U, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.groupby("t1", column);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> havingField(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.havingField("t0", column);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> havingField(Ex1SFunction<U, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.havingField("t1", column);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> orderbyAsc(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.orderbyAsc("t0", column);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> orderbyAsc(Ex1SFunction<U, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.orderbyAsc("t1", column);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> orderByDesc(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.orderByDesc("t0", column);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> orderByDesc(Ex1SFunction<U, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.orderByDesc("t1", column);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> distinct(Boolean distinct) {
    super.distinct(distinct);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> where() {
    super.where();
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> andStart() {
    super.andStart();
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> andEnd() {
    super.andEnd();
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> orStart() {
    super.orStart();
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> orEnd() {
    super.orEnd();
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> eq(String strValue) {
    super.eq(strValue);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> notEq(String strValue) {
    super.notEq(strValue);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> greater(String strValue) {
    super.greater(strValue);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> less(String strValue) {
    super.less(strValue);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> greaterEqual(String strValue) {
    super.greaterEqual(strValue);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> lessEqual(String strValue) {
    super.lessEqual(strValue);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> like(String strValue) {
    super.like(strValue);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> likeStart(String strValue) {
    super.likeStart(strValue);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> likeEnd(String strValue) {
    super.likeEnd(strValue);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> in(List list) {
    super.in(list);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> in(Collection list) {
    super.in(list);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> notIn(List list) {
    super.notIn(list);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> notIn(Collection list) {
    super.notIn(list);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> isNull() {
    super.isNull();
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> isNotNull() {
    super.isNotNull();
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> between(String strValue1, String strValue2) {
    super.between(strValue1, strValue2);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> page(Integer pageIndex, Integer size) {
    super.page(pageIndex, size);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> limit(Integer offset, Integer size) {
    super.limit(offset, size);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> havingOperator(OperatorEnum operatorEnum) {
    super.havingOperator(operatorEnum);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> havingStrValue(String strValue) {
    super.havingStrValue(strValue);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> havingStrValue(String strValue1, String strValue2) {
    super.havingStrValue(strValue1, strValue2);
    return this;
  }

  @Override
  public TwoEntityConditionBuilder<T, U> havingListValue(String strValue) {
    super.havingListValue(strValue);
    return this;
  }

  @Override
  public EntityCondition build() {
    return super.build();
  }
}
