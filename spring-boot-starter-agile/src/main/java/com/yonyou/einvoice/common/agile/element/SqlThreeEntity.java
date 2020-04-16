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
public class SqlThreeEntity<T, U, S> implements ISqlEntity {

  private Class<T> tClass;
  private Class<U> uClass;
  private Class<S> sClass;

  private SqlThreeEntity(Class<T> tClass, Class<U> uClass, Class<S> sClass) {
    this.tClass = tClass;
    this.uClass = uClass;
    this.sClass = sClass;
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

  public static <T, U, S> SqlThreeEntityBuilder<T, U, S> anBuilder(Class<T> tClass, Class<U> uClass,
      Class<S> sClass) {
    return new SqlThreeEntityBuilder<T, U, S>(tClass, uClass, sClass);
  }

  public static class SqlThreeEntityBuilder<T, U, S> implements IThreeEntityBuilder<T, U, S> {

    private List<SFunction> fields = new ArrayList<>();
    private ThreeEntityConditionBuilder<T, U, S> builder;
    private Class<T> tClass;
    private Class<U> uClass;
    private Class<S> sClass;

    private SqlThreeEntityBuilder(Class<T> tClass, Class<U> uClass, Class<S> sClass) {
      this.tClass = tClass;
      this.uClass = uClass;
      this.sClass = sClass;
      builder = EntityCondition.threeEntityConditionBuilder(tClass, uClass, sClass);
    }

    public SqlThreeEntityBuilder<T, U, S> selectFields(SFunction<T, ?>... selectFields) {
      fields.addAll(Arrays.asList(selectFields));
      return this;
    }

    public SqlThreeEntityBuilder<T, U, S> selectFields(Ex1SFunction<U, ?>... selectFields) {
      fields.addAll(Arrays.asList(selectFields));
      return this;
    }

    public SqlThreeEntityBuilder<T, U, S> selectFields(Ex2SFunction<S, ?>... selectFields) {
      fields.addAll(Arrays.asList(selectFields));
      return this;
    }

    @Override
    public <R> SqlThreeEntityBuilder<T, U, S> innerJoin(Class<R> rClass) {
      builder.innerJoin(rClass);
      return this;
    }

    @Override
    public <R> SqlThreeEntityBuilder<T, U, S> leftJoin(Class<R> rClass) {
      builder.leftJoin(rClass);
      return this;
    }

    @Override
    public <R> SqlThreeEntityBuilder<T, U, S> rightJoin(Class<R> rClass) {
      builder.rightJoin(rClass);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> on(SFunction<T, ?> sFunction1,
        SFunction<U, ?> sFunction2) {
      builder.on(sFunction1, sFunction2);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> on(Ex1SFunction<T, ?> sFunction1,
        Ex1SFunction<S, ?> sFunction2) {
      builder.on(sFunction1, sFunction2);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> field(SFunction<T, ?> sFunction) {
      builder.field(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> field(Ex1SFunction<U, ?> sFunction) {
      builder.field(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> field(Ex2SFunction<S, ?> sFunction) {
      builder.field(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> eq(SFunction<T, ?> sFunction) {
      builder.eq(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> eq(Ex1SFunction<U, ?> sFunction) {
      builder.eq(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> eq(Ex2SFunction<S, ?> sFunction) {
      builder.eq(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> notEq(SFunction<T, ?> sFunction) {
      builder.notEq(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> notEq(Ex1SFunction<U, ?> sFunction) {
      builder.notEq(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> notEq(Ex2SFunction<S, ?> sFunction) {
      builder.notEq(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> greater(SFunction<T, ?> sFunction) {
      builder.greater(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> greater(Ex1SFunction<U, ?> sFunction) {
      builder.greater(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> greater(Ex2SFunction<S, ?> sFunction) {
      builder.greater(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> less(SFunction<T, ?> sFunction) {
      builder.less(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> less(Ex1SFunction<U, ?> sFunction) {
      builder.less(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> less(Ex2SFunction<S, ?> sFunction) {
      builder.less(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> greaterEqual(SFunction<T, ?> sFunction) {
      builder.greaterEqual(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> greaterEqual(Ex1SFunction<U, ?> sFunction) {
      builder.greaterEqual(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> greaterEqual(Ex2SFunction<S, ?> sFunction) {
      builder.greaterEqual(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> lessEqual(SFunction<T, ?> sFunction) {
      builder.lessEqual(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> lessEqual(Ex1SFunction<U, ?> sFunction) {
      builder.lessEqual(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> lessEqual(Ex2SFunction<S, ?> sFunction) {
      builder.lessEqual(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> groupby(SFunction<T, ?> sFunction) {
      builder.groupby(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> groupby(Ex1SFunction<U, ?> sFunction) {
      builder.groupby(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> groupby(Ex2SFunction<S, ?> sFunction) {
      builder.groupby(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> havingField(SFunction<T, ?> sFunction) {
      builder.havingField(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> havingField(Ex1SFunction<U, ?> sFunction) {
      builder.havingField(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> havingField(Ex2SFunction<S, ?> sFunction) {
      builder.havingField(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> orderbyAsc(SFunction<T, ?> sFunction) {
      builder.orderbyAsc(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> orderbyAsc(Ex1SFunction<U, ?> sFunction) {
      builder.orderbyAsc(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> orderbyAsc(Ex2SFunction<S, ?> sFunction) {
      builder.orderbyAsc(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> orderByDesc(SFunction<T, ?> sFunction) {
      builder.orderByDesc(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> orderByDesc(Ex1SFunction<U, ?> sFunction) {
      builder.orderByDesc(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> orderByDesc(Ex2SFunction<S, ?> sFunction) {
      builder.orderByDesc(sFunction);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> distinct(Boolean distinct) {
      builder.distinct(distinct);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> where() {
      builder.where();
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> andStart() {
      builder.andStart();
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> andEnd() {
      builder.andEnd();
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> orStart() {
      builder.orStart();
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> orEnd() {
      builder.orEnd();
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> eq(String strValue) {
      builder.eq(strValue);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> notEq(String strValue) {
      builder.notEq(strValue);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> greater(String strValue) {
      builder.greater(strValue);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> less(String strValue) {
      builder.less(strValue);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> greaterEqual(String strValue) {
      builder.greaterEqual(strValue);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> lessEqual(String strValue) {
      builder.lessEqual(strValue);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> like(String strValue) {
      builder.like(strValue);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> likeStart(String strValue) {
      builder.likeStart(strValue);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> likeEnd(String strValue) {
      builder.likeEnd(strValue);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> in(List list) {
      builder.in(list);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> in(Collection list) {
      builder.in(list);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> notIn(List list) {
      builder.notIn(list);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> notIn(Collection list) {
      builder.notIn(list);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> isNull() {
      builder.isNull();
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> isNotNull() {
      builder.isNotNull();
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> between(String strValue1, String strValue2) {
      builder.between(strValue1, strValue2);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> page(Integer pageIndex, Integer size) {
      builder.page(pageIndex, size);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> limit(Integer offset, Integer size) {
      builder.limit(offset, size);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> havingOperator(OperatorEnum operatorEnum) {
      builder.havingOperator(operatorEnum);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> havingStrValue(String strValue) {
      builder.havingStrValue(strValue);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> havingStrValue(String strValue1, String strValue2) {
      builder.havingStrValue(strValue1, strValue2);
      return this;
    }

    @Override
    public SqlThreeEntityBuilder<T, U, S> havingListValue(String strValue) {
      builder.havingListValue(strValue);
      return this;
    }


    public SqlThreeEntity<T, U, S> build() {
      SqlThreeEntity<T, U, S> sqlOneEntity = new SqlThreeEntity<T, U, S>(tClass, uClass, sClass);
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
          } else if (Objects.equal(sClass, implClass)) {
            fieldStrList.add("t2." + column);
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
