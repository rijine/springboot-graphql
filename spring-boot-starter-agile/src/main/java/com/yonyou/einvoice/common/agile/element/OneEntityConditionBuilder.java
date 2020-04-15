package com.yonyou.einvoice.common.agile.element;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.yonyou.einvoice.common.agile.element.EntityCondition.EntityConditionBuilder;
import com.yonyou.einvoice.common.agile.enums.OperatorEnum;
import java.util.Collection;
import java.util.List;

public class OneEntityConditionBuilder<T> extends EntityConditionBuilder implements
    IOneEntityBuilder<T> {

  EntityConditionBuilder entityConditionBuilder = new EntityConditionBuilder();

  protected OneEntityConditionBuilder() {
  }

  /**
   * 支持lambda表达式指定field条件
   *
   * @param sFunction
   * @return
   */
  @Override
  public OneEntityConditionBuilder<T> field(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    entityConditionBuilder.field("t0", column);
    return this;
  }

  /**
   * 支持lambda表达式指定eq条件
   *
   * @param sFunction
   * @return
   */
  @Override
  public OneEntityConditionBuilder<T> eq(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    entityConditionBuilder.eq("t0", column);
    return this;
  }

  /**
   * 支持lambda表达式指定notEq条件
   *
   * @param sFunction
   * @return
   */
  @Override
  public OneEntityConditionBuilder<T> notEq(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    entityConditionBuilder.notEq("t0", column);
    return this;
  }

  /**
   * 支持lambda表达式指定greater条件
   *
   * @param sFunction
   * @return
   */
  @Override
  public OneEntityConditionBuilder<T> greater(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    entityConditionBuilder.greater("t0", column);
    return this;
  }

  /**
   * 支持lambda表达式指定greater条件
   *
   * @param sFunction
   * @return
   */
  @Override
  public OneEntityConditionBuilder<T> less(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    entityConditionBuilder.less("t0", column);
    return this;
  }

  /**
   * 支持lambda表达式指定greaterEqual条件
   *
   * @param sFunction
   * @return
   */
  @Override
  public OneEntityConditionBuilder<T> greaterEqual(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    entityConditionBuilder.greaterEqual("t0", column);
    return this;
  }

  /**
   * 支持lambda表达式指定lessEqual条件
   *
   * @param sFunction
   * @return
   */
  @Override
  public OneEntityConditionBuilder<T> lessEqual(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    entityConditionBuilder.lessEqual("t0", column);
    return this;
  }

  /**
   * 支持lambda表达式指定groupby条件
   *
   * @param sFunction
   * @return
   */
  @Override
  public OneEntityConditionBuilder<T> groupby(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    entityConditionBuilder.groupby("t0", column);
    return this;
  }

  /**
   * 支持lambda表达式指定havingField条件
   *
   * @param sFunction
   * @return
   */
  @Override
  public OneEntityConditionBuilder<T> havingField(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    entityConditionBuilder.havingField("t0", column);
    return this;
  }

  /**
   * 支持lambda表达式指定orderbyAsc条件
   *
   * @param sFunction
   * @return
   */
  @Override
  public OneEntityConditionBuilder<T> orderbyAsc(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    entityConditionBuilder.orderbyAsc("t0", column);
    return this;
  }

  /**
   * 支持lambda表达式指定orderByDesc条件
   *
   * @param sFunction
   * @return
   */
  @Override
  public OneEntityConditionBuilder<T> orderByDesc(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    entityConditionBuilder.orderByDesc("t0", column);
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> distinct(Boolean distinct) {
    entityConditionBuilder.distinct(distinct);
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> where() {
    entityConditionBuilder.where();
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> andStart() {
    entityConditionBuilder.andStart();
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> andEnd() {
    entityConditionBuilder.andEnd();
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> orStart() {
    entityConditionBuilder.orStart();
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> orEnd() {
    entityConditionBuilder.orEnd();
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> eq(String strValue) {
    entityConditionBuilder.eq(strValue);
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> notEq(String strValue) {
    entityConditionBuilder.notEq(strValue);
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> greater(String strValue) {
    entityConditionBuilder.greater(strValue);
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> less(String strValue) {
    entityConditionBuilder.less(strValue);
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> greaterEqual(String strValue) {
    entityConditionBuilder.greaterEqual(strValue);
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> lessEqual(String strValue) {
    entityConditionBuilder.lessEqual(strValue);
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> like(String strValue) {
    entityConditionBuilder.like(strValue);
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> likeStart(String strValue) {
    entityConditionBuilder.likeStart(strValue);
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> likeEnd(String strValue) {
    entityConditionBuilder.likeEnd(strValue);
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> in(List list) {
    entityConditionBuilder.in(list);
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> in(Collection list) {
    entityConditionBuilder.in(list);
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> notIn(List list) {
    entityConditionBuilder.notIn(list);
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> notIn(Collection list) {
    entityConditionBuilder.notIn(list);
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> isNull() {
    entityConditionBuilder.isNull();
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> isNotNull() {
    entityConditionBuilder.isNotNull();
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> between(String strValue1, String strValue2) {
    entityConditionBuilder.between(strValue1, strValue2);
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> page(Integer pageIndex, Integer size) {
    entityConditionBuilder.page(pageIndex, size);
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> limit(Integer offset, Integer size) {
    entityConditionBuilder.limit(offset, size);
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> havingOperator(OperatorEnum operatorEnum) {
    entityConditionBuilder.havingOperator(operatorEnum);
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> havingStrValue(String strValue) {
    entityConditionBuilder.havingStrValue(strValue);
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> havingStrValue(String strValue1, String strValue2) {
    entityConditionBuilder.havingStrValue(strValue1, strValue2);
    return this;
  }

  @Override
  public OneEntityConditionBuilder<T> havingListValue(String strValue) {
    entityConditionBuilder.havingListValue(strValue);
    return this;
  }

  @Override
  public EntityCondition build() {
    return entityConditionBuilder.build();
  }

}
