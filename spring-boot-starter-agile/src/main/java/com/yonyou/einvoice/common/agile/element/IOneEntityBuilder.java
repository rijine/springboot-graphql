package com.yonyou.einvoice.common.agile.element;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.yonyou.einvoice.common.agile.enums.OperatorEnum;
import java.util.Collection;
import java.util.List;

interface IOneEntityBuilder<T> {

  /**
   * 支持lambda表达式指定field条件
   *
   * @param sFunction
   * @return
   */
  IOneEntityBuilder<T> field(SFunction<T, ?> sFunction);

  /**
   * 支持lambda表达式指定eq条件
   *
   * @param sFunction
   * @return
   */
  IOneEntityBuilder<T> eq(SFunction<T, ?> sFunction);

  /**
   * 支持lambda表达式指定notEq条件
   *
   * @param sFunction
   * @return
   */
  IOneEntityBuilder<T> notEq(SFunction<T, ?> sFunction);

  /**
   * 支持lambda表达式指定greater条件
   *
   * @param sFunction
   * @return
   */
  IOneEntityBuilder<T> greater(SFunction<T, ?> sFunction);

  /**
   * 支持lambda表达式指定greater条件
   *
   * @param sFunction
   * @return
   */
  IOneEntityBuilder<T> less(SFunction<T, ?> sFunction);

  /**
   * 支持lambda表达式指定greaterEqual条件
   *
   * @param sFunction
   * @return
   */
  IOneEntityBuilder<T> greaterEqual(SFunction<T, ?> sFunction);

  /**
   * 支持lambda表达式指定lessEqual条件
   *
   * @param sFunction
   * @return
   */
  IOneEntityBuilder<T> lessEqual(SFunction<T, ?> sFunction);

  /**
   * 支持lambda表达式指定groupby条件
   *
   * @param sFunction
   * @return
   */
  IOneEntityBuilder<T> groupby(SFunction<T, ?> sFunction);

  /**
   * 支持lambda表达式指定havingField条件
   *
   * @param sFunction
   * @return
   */
  IOneEntityBuilder<T> havingField(SFunction<T, ?> sFunction);

  /**
   * 支持lambda表达式指定orderbyAsc条件
   *
   * @param sFunction
   * @return
   */
  IOneEntityBuilder<T> orderbyAsc(SFunction<T, ?> sFunction);

  /**
   * 支持lambda表达式指定orderByDesc条件
   *
   * @param sFunction
   * @return
   */
  IOneEntityBuilder<T> orderByDesc(SFunction<T, ?> sFunction);


  IOneEntityBuilder<T> distinct(Boolean distinct);

  IOneEntityBuilder<T> where();


  IOneEntityBuilder<T> andStart();


  IOneEntityBuilder<T> andEnd();


  IOneEntityBuilder<T> orStart();


  IOneEntityBuilder<T> orEnd();

  IOneEntityBuilder<T> eq(String strValue);

  IOneEntityBuilder<T> notEq(String strValue);

  IOneEntityBuilder<T> greater(String strValue);

  IOneEntityBuilder<T> less(String strValue);

  IOneEntityBuilder<T> greaterEqual(String strValue);

  IOneEntityBuilder<T> lessEqual(String strValue);

  IOneEntityBuilder<T> like(String strValue);


  IOneEntityBuilder<T> likeStart(String strValue);


  IOneEntityBuilder<T> likeEnd(String strValue);


  IOneEntityBuilder<T> in(List list);


  IOneEntityBuilder<T> in(Collection list);


  IOneEntityBuilder<T> notIn(List list);


  IOneEntityBuilder<T> notIn(Collection list);


  IOneEntityBuilder<T> isNull();


  IOneEntityBuilder<T> isNotNull();


  IOneEntityBuilder<T> between(String strValue1, String strValue2);

  IOneEntityBuilder<T> page(Integer pageIndex, Integer size);

  IOneEntityBuilder<T> limit(Integer offset, Integer size);

  IOneEntityBuilder<T> havingOperator(OperatorEnum operatorEnum);

  IOneEntityBuilder<T> havingStrValue(String strValue);

  IOneEntityBuilder<T> havingStrValue(String strValue1, String strValue2);

  IOneEntityBuilder<T> havingListValue(String strValue);

}
