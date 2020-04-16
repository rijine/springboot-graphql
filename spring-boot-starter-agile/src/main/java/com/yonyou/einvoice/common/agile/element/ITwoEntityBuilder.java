package com.yonyou.einvoice.common.agile.element;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.yonyou.einvoice.common.agile.enums.OperatorEnum;
import java.util.Collection;
import java.util.List;

interface ITwoEntityBuilder<T, U> {

  ITwoEntityBuilder<T, U> innerJoin(Class<U> uClass);

  ITwoEntityBuilder<T, U> leftJoin(Class<U> uClass);

  ITwoEntityBuilder<T, U> rightJoin(Class<U> uClass);

  /**
   * 支持lambda表达式拼接on查询条件
   *
   * @param sFunction1
   * @param sFunction2
   * @return
   */
  ITwoEntityBuilder<T, U> on(SFunction<T, ?> sFunction1,
      SFunction<U, ?> sFunction2);


  ITwoEntityBuilder<T, U> field(
      SFunction<T, ?> sFunction);

  ITwoEntityBuilder<T, U> field(
      Ex1SFunction<U, ?> sFunction);

  ITwoEntityBuilder<T, U> eq(SFunction<T, ?> sFunction);

  ITwoEntityBuilder<T, U> eq(Ex1SFunction<U, ?> sFunction);

  ITwoEntityBuilder<T, U> notEq(
      SFunction<T, ?> sFunction);

  ITwoEntityBuilder<T, U> notEq(
      Ex1SFunction<U, ?> sFunction);

  ITwoEntityBuilder<T, U> greater(
      SFunction<T, ?> sFunction);

  ITwoEntityBuilder<T, U> greater(
      Ex1SFunction<U, ?> sFunction);

  ITwoEntityBuilder<T, U> less(
      SFunction<T, ?> sFunction);

  ITwoEntityBuilder<T, U> less(
      Ex1SFunction<U, ?> sFunction);

  ITwoEntityBuilder<T, U> greaterEqual(
      SFunction<T, ?> sFunction);

  ITwoEntityBuilder<T, U> greaterEqual(
      Ex1SFunction<U, ?> sFunction);

  ITwoEntityBuilder<T, U> lessEqual(
      SFunction<T, ?> sFunction);

  ITwoEntityBuilder<T, U> lessEqual(
      Ex1SFunction<U, ?> sFunction);

  ITwoEntityBuilder<T, U> groupby(
      SFunction<T, ?> sFunction);

  ITwoEntityBuilder<T, U> groupby(
      Ex1SFunction<U, ?> sFunction);

  ITwoEntityBuilder<T, U> havingField(
      SFunction<T, ?> sFunction);

  ITwoEntityBuilder<T, U> havingField(
      Ex1SFunction<U, ?> sFunction);

  ITwoEntityBuilder<T, U> orderbyAsc(
      SFunction<T, ?> sFunction);

  ITwoEntityBuilder<T, U> orderbyAsc(
      Ex1SFunction<U, ?> sFunction);

  ITwoEntityBuilder<T, U> orderByDesc(
      SFunction<T, ?> sFunction);

  ITwoEntityBuilder<T, U> orderByDesc(
      Ex1SFunction<U, ?> sFunction);


  ITwoEntityBuilder<T, U> distinct(Boolean distinct);


  ITwoEntityBuilder<T, U> where();


  ITwoEntityBuilder<T, U> andStart();


  ITwoEntityBuilder<T, U> andEnd();


  ITwoEntityBuilder<T, U> orStart();


  ITwoEntityBuilder<T, U> orEnd();


  ITwoEntityBuilder<T, U> eq(String strValue);


  ITwoEntityBuilder<T, U> notEq(String strValue);


  ITwoEntityBuilder<T, U> greater(String strValue);


  ITwoEntityBuilder<T, U> less(String strValue);


  ITwoEntityBuilder<T, U> greaterEqual(String strValue);


  ITwoEntityBuilder<T, U> lessEqual(String strValue);


  ITwoEntityBuilder<T, U> like(String strValue);


  ITwoEntityBuilder<T, U> likeStart(String strValue);


  ITwoEntityBuilder<T, U> likeEnd(String strValue);


  ITwoEntityBuilder<T, U> in(List list);


  ITwoEntityBuilder<T, U> in(Collection list);


  ITwoEntityBuilder<T, U> notIn(List list);


  ITwoEntityBuilder<T, U> notIn(Collection list);


  ITwoEntityBuilder<T, U> isNull();


  ITwoEntityBuilder<T, U> isNotNull();


  ITwoEntityBuilder<T, U> between(String strValue1, String strValue2);


  ITwoEntityBuilder<T, U> page(Integer pageIndex, Integer size);


  ITwoEntityBuilder<T, U> limit(Integer offset, Integer size);


  ITwoEntityBuilder<T, U> havingOperator(OperatorEnum operatorEnum);


  ITwoEntityBuilder<T, U> havingStrValue(String strValue);


  ITwoEntityBuilder<T, U> havingStrValue(String strValue1, String strValue2);


  ITwoEntityBuilder<T, U> havingListValue(String strValue);
}
