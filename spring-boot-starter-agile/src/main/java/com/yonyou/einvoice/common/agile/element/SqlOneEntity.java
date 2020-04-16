package com.yonyou.einvoice.common.agile.element;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.yonyou.einvoice.common.agile.enums.OperatorEnum;
import com.yonyou.einvoice.common.agile.visitor.MybatisSqlVisitor;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.util.CollectionUtils;

/**
 * 单实体Sql的实体类型
 *
 * @author liuqiangm
 */
public class SqlOneEntity<T> implements ISqlEntity {

  private Class<T> tClass;

  private SqlOneEntity(Class<T> tClass) {
    this.tClass = tClass;
  }

  private List<SFunction> fields;
  private EntityCondition entityCondition;
  private MybatisSqlVisitor sqlVisitor;

  /**
   * 生成动态sql语句
   *
   * @return
   */
  @Override
  public String getSql() {
    return sqlVisitor.getSql();
  }

  @Override
  public Map<String, Object> getMybatisParamMap() {
    return sqlVisitor.getMybatisParamMap();
  }

  public static <T> SqlOneEntityBuilder<T> anBuilder(Class<T> tClass) {
    return new SqlOneEntityBuilder<>(tClass);
  }

  public static class SqlOneEntityBuilder<T> implements IOneEntityBuilder<T> {

    private List<SFunction> fields;
    private OneEntityConditionBuilder<T> builder;
    private Class<T> tClass;

    private SqlOneEntityBuilder(Class<T> tClass) {
      this.tClass = tClass;
      builder = EntityCondition.oneEntityConditionBuilder(tClass);
    }

    public SqlOneEntityBuilder<T> selectFields(SFunction<T, ?>... selectFields) {
      this.fields = Arrays.asList(selectFields);
      return this;
    }

    /**
     * 支持lambda表达式指定field条件
     *
     * @param sFunction
     * @return
     */
    @Override
    public SqlOneEntityBuilder<T> field(SFunction<T, ?> sFunction) {
      builder.field(sFunction);
      return this;
    }

    /**
     * 支持lambda表达式指定eq条件
     *
     * @param sFunction
     * @return
     */
    @Override
    public SqlOneEntityBuilder<T> eq(SFunction<T, ?> sFunction) {
      builder.eq(sFunction);
      return this;
    }

    /**
     * 支持lambda表达式指定notEq条件
     *
     * @param sFunction
     * @return
     */
    @Override
    public SqlOneEntityBuilder<T> notEq(SFunction<T, ?> sFunction) {
      builder.notEq(sFunction);
      return this;
    }

    /**
     * 支持lambda表达式指定greater条件
     *
     * @param sFunction
     * @return
     */
    @Override
    public SqlOneEntityBuilder<T> greater(SFunction<T, ?> sFunction) {
      builder.greater(sFunction);
      return this;
    }

    /**
     * 支持lambda表达式指定greater条件
     *
     * @param sFunction
     * @return
     */
    @Override
    public SqlOneEntityBuilder<T> less(SFunction<T, ?> sFunction) {
      builder.less(sFunction);
      return this;
    }

    /**
     * 支持lambda表达式指定greaterEqual条件
     *
     * @param sFunction
     * @return
     */
    @Override
    public SqlOneEntityBuilder<T> greaterEqual(SFunction<T, ?> sFunction) {
      builder.greaterEqual(sFunction);
      return this;
    }

    /**
     * 支持lambda表达式指定lessEqual条件
     *
     * @param sFunction
     * @return
     */
    @Override
    public SqlOneEntityBuilder<T> lessEqual(SFunction<T, ?> sFunction) {
      builder.lessEqual(sFunction);
      return this;
    }

    /**
     * 支持lambda表达式指定groupby条件
     *
     * @param sFunction
     * @return
     */
    @Override
    public SqlOneEntityBuilder<T> groupby(SFunction<T, ?> sFunction) {
      builder.groupby(sFunction);
      return this;
    }

    /**
     * 支持lambda表达式指定havingField条件
     *
     * @param sFunction
     * @return
     */
    @Override
    public SqlOneEntityBuilder<T> havingField(SFunction<T, ?> sFunction) {
      builder.havingField(sFunction);
      return this;
    }

    /**
     * 支持lambda表达式指定orderbyAsc条件
     *
     * @param sFunction
     * @return
     */
    @Override
    public SqlOneEntityBuilder<T> orderbyAsc(SFunction<T, ?> sFunction) {
      builder.orderbyAsc(sFunction);
      return this;
    }

    /**
     * 支持lambda表达式指定orderByDesc条件
     *
     * @param sFunction
     * @return
     */
    @Override
    public SqlOneEntityBuilder<T> orderByDesc(SFunction<T, ?> sFunction) {
      builder.orderByDesc(sFunction);
      return this;
    }


    @Override
    public SqlOneEntityBuilder<T> distinct(Boolean distinct) {
      builder.distinct(distinct);
      return this;
    }

    @Override
    public SqlOneEntityBuilder<T> where() {
      builder.where();
      return this;
    }


    @Override
    public SqlOneEntityBuilder<T> andStart() {
      builder.andStart();
      return this;
    }


    @Override
    public SqlOneEntityBuilder<T> andEnd() {
      builder.andEnd();
      return this;
    }


    @Override
    public SqlOneEntityBuilder<T> orStart() {
      builder.orStart();
      return this;
    }


    @Override
    public SqlOneEntityBuilder<T> orEnd() {
      builder.orEnd();
      return this;
    }

    @Override
    public SqlOneEntityBuilder<T> eq(String strValue) {
      builder.eq(strValue);
      return this;
    }

    @Override
    public SqlOneEntityBuilder<T> notEq(String strValue) {
      builder.notEq(strValue);
      return this;
    }

    @Override
    public SqlOneEntityBuilder<T> greater(String strValue) {
      builder.greater(strValue);
      return this;
    }


    @Override
    public SqlOneEntityBuilder<T> less(String strValue) {
      builder.less(strValue);
      return this;
    }

    @Override
    public SqlOneEntityBuilder<T> greaterEqual(String strValue) {
      builder.greaterEqual(strValue);
      return this;
    }

    @Override
    public SqlOneEntityBuilder<T> lessEqual(String strValue) {
      builder.lessEqual(strValue);
      return this;
    }

    @Override
    public SqlOneEntityBuilder<T> like(String strValue) {
      builder.like(strValue);
      return this;
    }


    @Override
    public SqlOneEntityBuilder<T> likeStart(String strValue) {
      builder.likeStart(strValue);
      return this;
    }


    @Override
    public SqlOneEntityBuilder<T> likeEnd(String strValue) {
      builder.likeEnd(strValue);
      return this;
    }


    @Override
    public SqlOneEntityBuilder<T> in(List list) {
      builder.in(list);
      return this;
    }


    @Override
    public SqlOneEntityBuilder<T> in(Collection list) {
      builder.in(list);
      return this;
    }


    @Override
    public SqlOneEntityBuilder<T> notIn(List list) {
      builder.notIn(list);
      return this;
    }


    @Override
    public SqlOneEntityBuilder<T> notIn(Collection list) {
      builder.notIn(list);
      return this;
    }


    @Override
    public SqlOneEntityBuilder<T> isNull() {
      builder.isNull();
      return this;
    }


    @Override
    public SqlOneEntityBuilder<T> isNotNull() {
      builder.isNotNull();
      return this;
    }


    @Override
    public SqlOneEntityBuilder<T> between(String strValue1, String strValue2) {
      builder.between(strValue1, strValue2);
      return this;
    }

    @Override
    public SqlOneEntityBuilder<T> page(Integer pageIndex, Integer size) {
      builder.page(pageIndex, size);
      return this;
    }


    @Override
    public SqlOneEntityBuilder<T> limit(Integer offset, Integer size) {
      builder.limit(offset, size);
      return this;
    }

    @Override
    public SqlOneEntityBuilder<T> havingOperator(OperatorEnum operatorEnum) {
      builder.havingOperator(operatorEnum);
      return this;
    }


    @Override
    public SqlOneEntityBuilder<T> havingStrValue(String strValue) {
      builder.havingStrValue(strValue);
      return this;
    }


    @Override
    public SqlOneEntityBuilder<T> havingStrValue(String strValue1, String strValue2) {
      builder.havingStrValue(strValue1, strValue2);
      return this;
    }


    @Override
    public SqlOneEntityBuilder<T> havingListValue(String strValue) {
      builder.havingListValue(strValue);
      return this;
    }

    public SqlOneEntity<T> build() {
      SqlOneEntity<T> sqlOneEntity = new SqlOneEntity<>(tClass);
      MybatisSqlVisitor sqlVisitor = new MybatisSqlVisitor();
      StringBuilder stringBuilder = sqlVisitor.getSqlBuilder();
      stringBuilder.append("select ");
      if (CollectionUtils.isEmpty(fields)) {
        stringBuilder.append("* ");
      } else {
        stringBuilder.append(
            fields.stream().map(field -> "t0." + EntityCondition.getColumnFromSFunction(field))
                .collect(
                    Collectors.joining(", "))).append(" ");
      }
      stringBuilder.append("from ").append(EntityCondition.getTableName(tClass)).append(" t0 ");
      sqlOneEntity.fields = this.fields;
      sqlOneEntity.sqlVisitor = sqlVisitor;
      sqlOneEntity.entityCondition = builder.build();
      sqlVisitor.visit(sqlOneEntity.entityCondition);
      return sqlOneEntity;
    }

  }


}
