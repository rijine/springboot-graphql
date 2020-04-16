package com.yonyou.einvoice.common.agile.element;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.google.common.base.Objects;
import com.yonyou.einvoice.common.agile.enums.OperatorEnum;
import com.yonyou.einvoice.common.agile.visitor.MybatisSqlVisitor;
import java.lang.invoke.SerializedLambda;
import java.util.ArrayList;
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
public class SqlTwoEntity<T, U> implements ISqlEntity {

  private Class<T> tClass;
  private Class<U> uClass;

  private SqlTwoEntity(Class<T> tClass, Class<U> uClass) {
    this.tClass = tClass;
    this.uClass = uClass;
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

  public static <T, U> SqlTwoEntityBuilder<T, U> anBuilder(Class<T> tClass, Class<U> uClass) {
    return new SqlTwoEntityBuilder<>(tClass, uClass);
  }

  public static class SqlTwoEntityBuilder<T, U> implements ITwoEntityBuilder<T, U> {

    private List<SFunction> fields = new ArrayList<>();
    private TwoEntityConditionBuilder<T, U> builder;
    private Class<T> tClass;
    private Class<U> uClass;

    private SqlTwoEntityBuilder(Class<T> tClass, Class<U> uClass) {
      this.tClass = tClass;
      this.uClass = uClass;
      builder = EntityCondition.twoEntityConditionBuilder(tClass, uClass);
    }

    public SqlTwoEntityBuilder<T, U> selectFields(SFunction<T, ?>... selectFields) {
      fields.addAll(Arrays.asList(selectFields));
      return this;
    }

    public SqlTwoEntityBuilder<T, U> selectFields(Ex1SFunction<U, ?>... selectFields) {
      fields.addAll(Arrays.asList(selectFields));
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> innerJoin(Class<U> uClass) {
      builder.innerJoin(uClass);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> leftJoin(Class<U> uClass) {
      builder.leftJoin(uClass);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> rightJoin(Class<U> uClass) {
      builder.rightJoin(uClass);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> on(SFunction<T, ?> sFunction1, SFunction<U, ?> sFunction2) {
      builder.on(sFunction1, sFunction2);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> field(SFunction<T, ?> sFunction) {
      builder.field(sFunction);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> field(Ex1SFunction<U, ?> sFunction) {
      builder.field(sFunction);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> eq(SFunction<T, ?> sFunction) {
      builder.eq(sFunction);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> eq(Ex1SFunction<U, ?> sFunction) {
      builder.eq(sFunction);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> notEq(SFunction<T, ?> sFunction) {
      builder.notEq(sFunction);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> notEq(Ex1SFunction<U, ?> sFunction) {
      builder.notEq(sFunction);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> greater(SFunction<T, ?> sFunction) {
      builder.greater(sFunction);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> greater(Ex1SFunction<U, ?> sFunction) {
      builder.greater(sFunction);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> less(SFunction<T, ?> sFunction) {
      builder.less(sFunction);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> less(Ex1SFunction<U, ?> sFunction) {
      builder.less(sFunction);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> greaterEqual(SFunction<T, ?> sFunction) {
      builder.greaterEqual(sFunction);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> greaterEqual(Ex1SFunction<U, ?> sFunction) {
      builder.greaterEqual(sFunction);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> lessEqual(SFunction<T, ?> sFunction) {
      builder.lessEqual(sFunction);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> lessEqual(Ex1SFunction<U, ?> sFunction) {
      builder.lessEqual(sFunction);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> groupby(SFunction<T, ?> sFunction) {
      builder.groupby(sFunction);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> groupby(Ex1SFunction<U, ?> sFunction) {
      builder.groupby(sFunction);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> havingField(SFunction<T, ?> sFunction) {
      builder.havingField(sFunction);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> havingField(Ex1SFunction<U, ?> sFunction) {
      builder.havingField(sFunction);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> orderbyAsc(SFunction<T, ?> sFunction) {
      builder.orderbyAsc(sFunction);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> orderbyAsc(Ex1SFunction<U, ?> sFunction) {
      builder.orderbyAsc(sFunction);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> orderByDesc(SFunction<T, ?> sFunction) {
      builder.orderByDesc(sFunction);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> orderByDesc(Ex1SFunction<U, ?> sFunction) {
      builder.orderByDesc(sFunction);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> distinct(Boolean distinct) {
      builder.distinct(distinct);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> where() {
      builder.where();
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> andStart() {
      builder.andStart();
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> andEnd() {
      builder.andEnd();
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> orStart() {
      builder.orStart();
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> orEnd() {
      builder.orEnd();
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> eq(String strValue) {
      builder.eq(strValue);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> notEq(String strValue) {
      builder.notEq(strValue);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> greater(String strValue) {
      builder.greater(strValue);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> less(String strValue) {
      builder.less(strValue);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> greaterEqual(String strValue) {
      builder.greaterEqual(strValue);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> lessEqual(String strValue) {
      builder.lessEqual(strValue);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> like(String strValue) {
      builder.like(strValue);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> likeStart(String strValue) {
      builder.likeStart(strValue);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> likeEnd(String strValue) {
      builder.likeEnd(strValue);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> in(List list) {
      builder.in(list);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> in(Collection list) {
      builder.in(list);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> notIn(List list) {
      builder.notIn(list);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> notIn(Collection list) {
      builder.notIn(list);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> isNull() {
      builder.isNull();
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> isNotNull() {
      builder.isNotNull();
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> between(String strValue1, String strValue2) {
      builder.between(strValue1, strValue2);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> page(Integer pageIndex, Integer size) {
      builder.page(pageIndex, size);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> limit(Integer offset, Integer size) {
      builder.limit(offset, size);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> havingOperator(OperatorEnum operatorEnum) {
      builder.havingOperator(operatorEnum);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> havingStrValue(String strValue) {
      builder.havingStrValue(strValue);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> havingStrValue(String strValue1, String strValue2) {
      builder.havingStrValue(strValue1, strValue2);
      return this;
    }

    @Override
    public SqlTwoEntityBuilder<T, U> havingListValue(String strValue) {
      builder.havingListValue(strValue);
      return this;
    }

    public SqlTwoEntity<T, U> build() {
      SqlTwoEntity<T, U> sqlOneEntity = new SqlTwoEntity<T, U>(tClass, uClass);
      MybatisSqlVisitor sqlVisitor = new MybatisSqlVisitor();
      StringBuilder stringBuilder = sqlVisitor.getSqlBuilder();
      stringBuilder.append("select ");
      if (CollectionUtils.isEmpty(fields)) {
        stringBuilder.append("* ");
      } else {
        List<String> fieldStrList = new ArrayList<>(fields.size());
        for (SFunction field : fields) {
          SerializedLambda serializedLambda = EntityCondition
              .getSerializedLambdaFromSFunction(field);
          Class implClass = EntityCondition.getImplClassFromSerializedLambda(serializedLambda);
          String column = EntityCondition.getColumnFromClass(implClass, serializedLambda);
          if (Objects.equal(tClass, implClass)) {
            fieldStrList.add("t0." + column);
          } else if (Objects.equal(uClass, implClass)) {
            fieldStrList.add("t1." + column);
          } else {
            throw new RuntimeException(
                "select字段的field只能在" + tClass.getName() + "和" + uClass.getName() + "中选择");
          }
        }
        stringBuilder.append(fieldStrList.stream().collect(
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
