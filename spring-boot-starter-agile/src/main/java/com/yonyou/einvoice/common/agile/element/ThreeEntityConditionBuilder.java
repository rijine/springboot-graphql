package com.yonyou.einvoice.common.agile.element;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.google.common.base.Objects;
import com.yonyou.einvoice.common.agile.element.EntityCondition.EntityConditionBuilder;
import com.yonyou.einvoice.common.agile.enums.OperatorEnum;
import java.util.Collection;
import java.util.List;

public class ThreeEntityConditionBuilder<T, U, S> extends
    EntityConditionBuilder implements IThreeEntityBuilder<T, U, S> {

  private Class<T> tClass;
  private Class<U> uClass;
  private Class<S> sClass;

  protected ThreeEntityConditionBuilder(Class<T> tClass, Class<U> uClass, Class<S> sClass) {
    this.tClass = tClass;
    this.uClass = uClass;
    this.sClass = sClass;
  }

  @Override
  public <R> ThreeEntityConditionBuilder<T, U, S> innerJoin(
      Class<R> rClass) {
    if (Objects.equal(uClass, rClass)) {
      this.innerJoin(EntityCondition.getTableName(uClass), "t1");
    } else if (Objects.equal(sClass, rClass)) {
      this.innerJoin(EntityCondition.getTableName(rClass), "t2");
    } else {
      throw new RuntimeException(
          "传入的Function接口的所属class类：" + rClass.getName() + "与泛型类：" + uClass.getName() + "/" + sClass
              .getName() + "不一致");
    }
    return this;
  }

  @Override
  public <R> ThreeEntityConditionBuilder<T, U, S> leftJoin(Class<R> rClass) {
    if (Objects.equal(uClass, rClass)) {
      this.leftJoin(EntityCondition.getTableName(uClass), "t1");
    } else if (Objects.equal(sClass, rClass)) {
      this.leftJoin(EntityCondition.getTableName(rClass), "t2");
    } else {
      throw new RuntimeException(
          "传入的Function接口的所属class类：" + rClass.getName() + "与泛型类：" + uClass.getName() + "/" + sClass
              .getName() + "不一致");
    }
    return this;
  }

  @Override
  public <R> ThreeEntityConditionBuilder<T, U, S> rightJoin(
      Class<R> rClass) {
    if (Objects.equal(uClass, rClass)) {
      this.rightJoin(EntityCondition.getTableName(uClass), "t1");
    } else if (Objects.equal(sClass, rClass)) {
      this.rightJoin(EntityCondition.getTableName(rClass), "t2");
    } else {
      throw new RuntimeException(
          "传入的Function接口的所属class类：" + rClass.getName() + "与泛型类：" + uClass.getName() + "/" + sClass
              .getName() + "不一致");
    }
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> on(SFunction<T, ?> sFunction1,
      SFunction<U, ?> sFunction2) {
    String column1 = EntityCondition.getColumnFromSFunction(sFunction1);
    String column2 = EntityCondition.getColumnFromSFunction(sFunction2);
    this.on("t0", column1, "t1", column2);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> on(Ex1SFunction<T, ?> sFunction1,
      Ex1SFunction<S, ?> sFunction2) {
    String column1 = EntityCondition.getColumnFromSFunction(sFunction1);
    String column2 = EntityCondition.getColumnFromSFunction(sFunction2);
    this.on("t0", column1, "t2", column2);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> field(
      SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.field("t0", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> field(
      Ex1SFunction<U, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.field("t1", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> field(
      Ex2SFunction<S, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.field("t2", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> eq(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.eq("t0", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> eq(Ex1SFunction<U, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.eq("t1", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> eq(Ex2SFunction<S, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.eq("t2", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> notEq(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.notEq("t0", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> notEq(Ex1SFunction<U, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.notEq("t1", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> notEq(Ex2SFunction<S, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.notEq("t2", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> greater(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.greater("t0", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> greater(Ex1SFunction<U, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.greater("t1", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> greater(Ex2SFunction<S, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.greater("t2", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> less(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.less("t0", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> less(Ex1SFunction<U, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.less("t1", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> less(Ex2SFunction<S, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.less("t2", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> greaterEqual(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.greaterEqual("t0", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> greaterEqual(Ex1SFunction<U, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.greaterEqual("t1", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> greaterEqual(Ex2SFunction<S, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.greaterEqual("t2", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> lessEqual(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.lessEqual("t0", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> lessEqual(Ex1SFunction<U, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.lessEqual("t1", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> lessEqual(Ex2SFunction<S, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.lessEqual("t2", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> groupby(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.groupby("t0", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> groupby(Ex1SFunction<U, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.groupby("t1", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> groupby(Ex2SFunction<S, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.groupby("t2", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> havingField(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.havingField("t0", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> havingField(Ex1SFunction<U, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.havingField("t1", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> havingField(Ex2SFunction<S, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.havingField("t2", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> orderbyAsc(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.orderbyAsc("t0", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> orderbyAsc(Ex1SFunction<U, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.orderbyAsc("t1", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> orderbyAsc(Ex2SFunction<S, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.orderbyAsc("t2", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> orderByDesc(SFunction<T, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.orderByDesc("t0", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> orderByDesc(Ex1SFunction<U, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.orderByDesc("t1", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> orderByDesc(Ex2SFunction<S, ?> sFunction) {
    String column = EntityCondition.getColumnFromSFunction(sFunction);
    this.orderByDesc("t2", column);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> distinct(Boolean distinct) {
    super.distinct(distinct);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> where() {
    super.where();
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> andStart() {
    super.andStart();
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> andEnd() {
    super.andEnd();
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> orStart() {
    super.orStart();
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> orEnd() {
    super.orEnd();
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> eq(String strValue) {
    super.eq(strValue);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> notEq(String strValue) {
    super.notEq(strValue);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> greater(String strValue) {
    super.greater(strValue);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> less(String strValue) {
    super.less(strValue);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> greaterEqual(String strValue) {
    super.greaterEqual(strValue);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> lessEqual(String strValue) {
    super.lessEqual(strValue);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> like(String strValue) {
    super.like(strValue);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> likeStart(String strValue) {
    super.likeStart(strValue);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> likeEnd(String strValue) {
    super.likeEnd(strValue);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> in(List list) {
    super.in(list);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> in(Collection list) {
    super.in(list);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> notIn(List list) {
    super.notIn(list);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> notIn(Collection list) {
    super.notIn(list);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> isNull() {
    super.isNull();
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> isNotNull() {
    super.isNotNull();
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> between(String strValue1, String strValue2) {
    super.between(strValue1, strValue2);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> page(Integer pageIndex, Integer size) {
    super.page(pageIndex, size);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> limit(Integer offset, Integer size) {
    super.limit(offset, size);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> havingOperator(OperatorEnum operatorEnum) {
    super.havingOperator(operatorEnum);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> havingStrValue(String strValue) {
    super.havingStrValue(strValue);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> havingStrValue(String strValue1, String strValue2) {
    super.havingStrValue(strValue1, strValue2);
    return this;
  }

  @Override
  public ThreeEntityConditionBuilder<T, U, S> havingListValue(String strValue) {
    super.havingListValue(strValue);
    return this;
  }

  @Override
  public EntityCondition build() {
    return super.build();
  }
}
