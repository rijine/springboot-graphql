package com.yonyou.einvoice.common.agile.element;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.yonyou.einvoice.common.agile.enums.OperatorEnum;
import java.util.Collection;
import java.util.List;

interface IThreeEntityBuilder<T, U, S> {

  <R> IThreeEntityBuilder<T, U, S> innerJoin(
      Class<R> rClass);

  <R> IThreeEntityBuilder<T, U, S> leftJoin(Class<R> rClass);

  <R> IThreeEntityBuilder<T, U, S> rightJoin(
      Class<R> rClass);

  IThreeEntityBuilder<T, U, S> on(SFunction<T, ?> sFunction1, SFunction<U, ?> sFunction2);

  IThreeEntityBuilder<T, U, S> on(Ex1SFunction<T, ?> sFunction1, Ex1SFunction<S, ?> sFunction2);

  IThreeEntityBuilder<T, U, S> field(
      SFunction<T, ?> sFunction);

  IThreeEntityBuilder<T, U, S> field(
      Ex1SFunction<U, ?> sFunction);

  IThreeEntityBuilder<T, U, S> field(
      Ex2SFunction<S, ?> sFunction);

  IThreeEntityBuilder<T, U, S> eq(
      SFunction<T, ?> sFunction);

  IThreeEntityBuilder<T, U, S> eq(
      Ex1SFunction<U, ?> sFunction);

  IThreeEntityBuilder<T, U, S> eq(
      Ex2SFunction<S, ?> sFunction);

  IThreeEntityBuilder<T, U, S> notEq(
      SFunction<T, ?> sFunction);

  IThreeEntityBuilder<T, U, S> notEq(
      Ex1SFunction<U, ?> sFunction);

  IThreeEntityBuilder<T, U, S> notEq(
      Ex2SFunction<S, ?> sFunction);

  IThreeEntityBuilder<T, U, S> greater(
      SFunction<T, ?> sFunction);

  IThreeEntityBuilder<T, U, S> greater(
      Ex1SFunction<U, ?> sFunction);

  IThreeEntityBuilder<T, U, S> greater(
      Ex2SFunction<S, ?> sFunction);

  IThreeEntityBuilder<T, U, S> less(
      SFunction<T, ?> sFunction);

  IThreeEntityBuilder<T, U, S> less(
      Ex1SFunction<U, ?> sFunction);

  IThreeEntityBuilder<T, U, S> less(
      Ex2SFunction<S, ?> sFunction);

  IThreeEntityBuilder<T, U, S> greaterEqual(
      SFunction<T, ?> sFunction);

  IThreeEntityBuilder<T, U, S> greaterEqual(
      Ex1SFunction<U, ?> sFunction);

  IThreeEntityBuilder<T, U, S> greaterEqual(
      Ex2SFunction<S, ?> sFunction);

  IThreeEntityBuilder<T, U, S> lessEqual(
      SFunction<T, ?> sFunction);

  IThreeEntityBuilder<T, U, S> lessEqual(
      Ex1SFunction<U, ?> sFunction);

  IThreeEntityBuilder<T, U, S> lessEqual(
      Ex2SFunction<S, ?> sFunction);

  IThreeEntityBuilder<T, U, S> groupby(
      SFunction<T, ?> sFunction);

  IThreeEntityBuilder<T, U, S> groupby(
      Ex1SFunction<U, ?> sFunction);

  IThreeEntityBuilder<T, U, S> groupby(
      Ex2SFunction<S, ?> sFunction);

  IThreeEntityBuilder<T, U, S> havingField(
      SFunction<T, ?> sFunction);

  IThreeEntityBuilder<T, U, S> havingField(
      Ex1SFunction<U, ?> sFunction);

  IThreeEntityBuilder<T, U, S> havingField(
      Ex2SFunction<S, ?> sFunction);

  IThreeEntityBuilder<T, U, S> orderbyAsc(
      SFunction<T, ?> sFunction);

  IThreeEntityBuilder<T, U, S> orderbyAsc(
      Ex1SFunction<U, ?> sFunction);

  IThreeEntityBuilder<T, U, S> orderbyAsc(
      Ex2SFunction<S, ?> sFunction);

  IThreeEntityBuilder<T, U, S> orderByDesc(
      SFunction<T, ?> sFunction);

  IThreeEntityBuilder<T, U, S> orderByDesc(
      Ex1SFunction<U, ?> sFunction);

  IThreeEntityBuilder<T, U, S> orderByDesc(
      Ex2SFunction<S, ?> sFunction);

  IThreeEntityBuilder<T, U, S> distinct(Boolean distinct);

  IThreeEntityBuilder<T, U, S> where();

  IThreeEntityBuilder<T, U, S> andStart();

  IThreeEntityBuilder<T, U, S> andEnd();

  IThreeEntityBuilder<T, U, S> orStart();

  IThreeEntityBuilder<T, U, S> orEnd();

  IThreeEntityBuilder<T, U, S> eq(String strValue);

  IThreeEntityBuilder<T, U, S> notEq(String strValue);

  IThreeEntityBuilder<T, U, S> greater(String strValue);

  IThreeEntityBuilder<T, U, S> less(String strValue);

  IThreeEntityBuilder<T, U, S> greaterEqual(String strValue);

  IThreeEntityBuilder<T, U, S> lessEqual(String strValue);

  IThreeEntityBuilder<T, U, S> like(String strValue);

  IThreeEntityBuilder<T, U, S> likeStart(String strValue);

  IThreeEntityBuilder<T, U, S> likeEnd(String strValue);

  IThreeEntityBuilder<T, U, S> in(List list);

  IThreeEntityBuilder<T, U, S> in(Collection list);

  IThreeEntityBuilder<T, U, S> notIn(List list);

  IThreeEntityBuilder<T, U, S> notIn(Collection list);

  IThreeEntityBuilder<T, U, S> isNull();

  IThreeEntityBuilder<T, U, S> isNotNull();

  IThreeEntityBuilder<T, U, S> between(String strValue1, String strValue2);

  IThreeEntityBuilder<T, U, S> page(Integer pageIndex, Integer size);

  IThreeEntityBuilder<T, U, S> limit(Integer offset, Integer size);

  IThreeEntityBuilder<T, U, S> havingOperator(OperatorEnum operatorEnum);

  IThreeEntityBuilder<T, U, S> havingStrValue(String strValue);

  IThreeEntityBuilder<T, U, S> havingStrValue(String strValue1, String strValue2);

  IThreeEntityBuilder<T, U, S> havingListValue(String strValue);
}
