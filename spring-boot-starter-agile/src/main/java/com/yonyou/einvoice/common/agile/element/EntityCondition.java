package com.yonyou.einvoice.common.agile.element;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.yonyou.einvoice.common.agile.entity.IAgileEntity;
import com.yonyou.einvoice.common.agile.enums.DirectionEnum;
import com.yonyou.einvoice.common.agile.enums.JointypeEnum;
import com.yonyou.einvoice.common.agile.enums.OperatorEnum;
import com.yonyou.einvoice.common.agile.visitor.IMetaElement;
import com.yonyou.einvoice.common.agile.visitor.IVisitor;
import io.leangen.graphql.annotations.GraphQLIgnore;
import io.leangen.graphql.annotations.types.GraphQLType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.reflection.property.PropertyNamer;
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

  @GraphQLIgnore
  public static <T extends IAgileEntity> OneEntityConditionBuilder<T> oneEntityBuilder(Class<T> clazz) {
    return new OneEntityConditionBuilder<T>();
  }

  public static class TwoEntityConditionBuilder<T extends IAgileEntity, U extends IAgileEntity> extends EntityConditionBuilder {

    protected TwoEntityConditionBuilder() {

    }

    /**
     * 支持lambda表达式拼接on查询条件
     * @param sFunction1
     * @param sFunction2
     * @return
     */
    public TwoEntityConditionBuilder<T, U> on(SFunction<T, ?> sFunction1, SFunction<U, ?> sFunction2) {
      String column1 = getColumnFromSFunction(sFunction1);
      String column2 = getColumnFromSFunction(sFunction2);
      this.on("t0", column1, "t1", column2);
      return this;
    }

    /**
     * 支持lambda表达式指定field条件
     * @param sFunction
     * @return
     */
    public TwoEntityConditionBuilder<T, U> field(SFunction sFunction) {
      String column = getColumnFromSFunction(sFunction);
      this.field("t0", column);
      return this;
    }

    /**
     * 支持lambda表达式指定eq条件
     * @param sFunction
     * @return
     */
    public TwoEntityConditionBuilder<T, U> eq(SFunction<T, ?> sFunction) {
      String column = getColumnFromSFunction(sFunction);
      this.eq("t0", column);
      return this;
    }

    /**
     * 支持lambda表达式指定notEq条件
     * @param sFunction
     * @return
     */
    public TwoEntityConditionBuilder<T, U> notEq(SFunction<T, ?> sFunction) {
      String column = getColumnFromSFunction(sFunction);
      this.notEq("t0", column);
      return this;
    }

    /**
     * 支持lambda表达式指定greater条件
     * @param sFunction
     * @return
     */
    public TwoEntityConditionBuilder<T, U> greater(SFunction<T, ?> sFunction) {
      String column = getColumnFromSFunction(sFunction);
      this.greater("t0", column);
      return this;
    }

    /**
     * 支持lambda表达式指定greater条件
     * @param sFunction
     * @return
     */
    public TwoEntityConditionBuilder<T, U> less(SFunction<T, ?> sFunction) {
      String column = getColumnFromSFunction(sFunction);
      this.less("t0", column);
      return this;
    }

    /**
     * 支持lambda表达式指定greaterEqual条件
     * @param sFunction
     * @return
     */
    public TwoEntityConditionBuilder<T, U> greaterEqual(SFunction<T, ?> sFunction) {
      String column = getColumnFromSFunction(sFunction);
      this.greaterEqual("t0", column);
      return this;
    }

    /**
     * 支持lambda表达式指定lessEqual条件
     * @param sFunction
     * @return
     */
    public TwoEntityConditionBuilder<T, U> lessEqual(SFunction<T, ?> sFunction) {
      String column = getColumnFromSFunction(sFunction);
      this.lessEqual("t0", column);
      return this;
    }

    /**
     * 支持lambda表达式指定groupby条件
     * @param sFunction
     * @return
     */
    public TwoEntityConditionBuilder<T, U> groupby(SFunction<T, ?> sFunction) {
      String column = getColumnFromSFunction(sFunction);
      super.groupby("t0", column);
      return this;
    }

    /**
     * 支持lambda表达式指定havingField条件
     * @param sFunction
     * @return
     */
    public TwoEntityConditionBuilder<T, U> havingField(SFunction<T, ?> sFunction) {
      String column = getColumnFromSFunction(sFunction);
      super.havingField("t0", column);
      return this;
    }

    /**
     * 支持lambda表达式指定orderbyAsc条件
     * @param sFunction
     * @return
     */
    public TwoEntityConditionBuilder<T, U> orderbyAsc(SFunction<T, ?> sFunction) {
      String column = getColumnFromSFunction(sFunction);
      super.orderbyAsc("t0", column);
      return this;
    }

    /**
     * 支持lambda表达式指定orderByDesc条件
     * @param sFunction
     * @return
     */
    public TwoEntityConditionBuilder<T, U> orderByDesc(SFunction<T, ?> sFunction) {
      String column = getColumnFromSFunction(sFunction);
      super.orderByDesc("t0", column);
      return this;
    }

    @Override
    public TwoEntityConditionBuilder<T, U> distinct(Boolean distinct) {
      super.distinct(distinct);
      return this;
    }

    @Override
    public TwoEntityConditionBuilder<T, U> innerJoin(String target, String alias) {
      super.innerJoin(target, alias);
      return this;
    }

    @Override
    public TwoEntityConditionBuilder<T, U> leftJoin(String target, String alias) {
      super.leftJoin(target, alias);
      return this;
    }

    @Override
    public TwoEntityConditionBuilder<T, U> rightJoin(String target, String alias) {
      super.rightJoin(target, alias);
      return this;
    }

    @Override
    public TwoEntityConditionBuilder<T, U> on(String sourceAlias1, String field1, String sourceAlias2,
        String field2) {
      super.on(sourceAlias1, field1, sourceAlias2, field2);
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
    public TwoEntityConditionBuilder<T, U> field(String sourceAlias, String sourceField) {
      super.field(sourceAlias, sourceField);
      return this;
    }

    @Override
    public TwoEntityConditionBuilder<T, U> eq(String strValue) {
      super.eq(strValue);
      return this;
    }

    @Override
    public TwoEntityConditionBuilder<T, U> eq(String sourceAlias, String sourceField) {
      super.eq(sourceAlias, sourceField);
      return this;
    }

    @Override
    public TwoEntityConditionBuilder<T, U> notEq(String strValue) {
      super.notEq(strValue);
      return this;
    }

    @Override
    public TwoEntityConditionBuilder<T, U> notEq(String sourceAlias, String sourceField) {
      super.notEq(sourceAlias, sourceField);
      return this;
    }

    @Override
    public TwoEntityConditionBuilder<T, U> greater(String strValue) {
      super.greater(strValue);
      return this;
    }

    @Override
    public TwoEntityConditionBuilder<T, U> greater(String sourceAlias, String sourceField) {
      super.greater(sourceAlias, sourceField);
      return this;
    }

    @Override
    public TwoEntityConditionBuilder<T, U> less(String strValue) {
      super.less(strValue);
      return this;
    }

    @Override
    public TwoEntityConditionBuilder<T, U> less(String sourceAlias, String sourceField) {
      super.less(sourceAlias, sourceField);
      return this;
    }

    @Override
    public TwoEntityConditionBuilder<T, U> greaterEqual(String strValue) {
      super.greaterEqual(strValue);
      return this;
    }

    @Override
    public TwoEntityConditionBuilder<T, U> greaterEqual(String sourceAlias, String sourceField) {
      super.greaterEqual(sourceAlias, sourceField);
      return this;
    }

    @Override
    public TwoEntityConditionBuilder<T, U> lessEqual(String strValue) {
      super.lessEqual(strValue);
      return this;
    }

    @Override
    public TwoEntityConditionBuilder<T, U> lessEqual(String sourceAlias, String sourceField) {
      super.lessEqual(sourceAlias, sourceField);
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
    public TwoEntityConditionBuilder<T, U> groupby(String sourceAlias, String field) {
      super.groupby(sourceAlias, field);
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
    public TwoEntityConditionBuilder<T, U> havingField(String sourceAlias, String sourceField) {
      super.havingField(sourceAlias, sourceField);
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
    public TwoEntityConditionBuilder<T, U> orderbyAsc(String sourceAlias, String sourceField) {
      super.orderbyAsc(sourceAlias, sourceField);
      return this;
    }

    @Override
    public TwoEntityConditionBuilder<T, U> orderByDesc(String sourceAlias, String sourceField) {
      super.orderByDesc(sourceAlias, sourceField);
      return this;
    }

    @Override
    public EntityCondition build() {
      return super.build();
    }
  }

  public static class OneEntityConditionBuilder<T extends IAgileEntity> extends EntityConditionBuilder {

    protected OneEntityConditionBuilder() {
      ;
    }

    /**
     * 支持lambda表达式拼接on查询条件
     * @param sFunction
     * @param sourceAlias2
     * @param field2
     * @return
     */
    public OneEntityConditionBuilder<T> on(SFunction<T, ?> sFunction, String sourceAlias2,
        String field2) {
      String column = getColumnFromSFunction(sFunction);
      this.on("t0", column, sourceAlias2, field2);
      return this;
    }

    /**
     * 支持lambda表达式指定field条件
     * @param sFunction
     * @return
     */
    public OneEntityConditionBuilder<T> field(SFunction<T, ?> sFunction) {
      String column = getColumnFromSFunction(sFunction);
      this.field("t0", column);
      return this;
    }

    /**
     * 支持lambda表达式指定eq条件
     * @param sFunction
     * @return
     */
    public OneEntityConditionBuilder<T> eq(SFunction<T, ?> sFunction) {
      String column = getColumnFromSFunction(sFunction);
      this.eq("t0", column);
      return this;
    }

    /**
     * 支持lambda表达式指定notEq条件
     * @param sFunction
     * @return
     */
    public OneEntityConditionBuilder<T> notEq(SFunction<T, ?> sFunction) {
      String column = getColumnFromSFunction(sFunction);
      this.notEq("t0", column);
      return this;
    }

    /**
     * 支持lambda表达式指定greater条件
     * @param sFunction
     * @return
     */
    public OneEntityConditionBuilder<T> greater(SFunction<T, ?> sFunction) {
      String column = getColumnFromSFunction(sFunction);
      this.greater("t0", column);
      return this;
    }

    /**
     * 支持lambda表达式指定greater条件
     * @param sFunction
     * @return
     */
    public OneEntityConditionBuilder<T> less(SFunction<T, ?> sFunction) {
      String column = getColumnFromSFunction(sFunction);
      this.less("t0", column);
      return this;
    }

    /**
     * 支持lambda表达式指定greaterEqual条件
     * @param sFunction
     * @return
     */
    public OneEntityConditionBuilder<T> greaterEqual(SFunction<T, ?> sFunction) {
      String column = getColumnFromSFunction(sFunction);
      this.greaterEqual("t0", column);
      return this;
    }

    /**
     * 支持lambda表达式指定lessEqual条件
     * @param sFunction
     * @return
     */
    public OneEntityConditionBuilder<T> lessEqual(SFunction<T, ?> sFunction) {
      String column = getColumnFromSFunction(sFunction);
      this.lessEqual("t0", column);
      return this;
    }

    /**
     * 支持lambda表达式指定groupby条件
     * @param sFunction
     * @return
     */
    public OneEntityConditionBuilder<T> groupby(SFunction<T, ?> sFunction) {
      String column = getColumnFromSFunction(sFunction);
      super.groupby("t0", column);
      return this;
    }

    /**
     * 支持lambda表达式指定havingField条件
     * @param sFunction
     * @return
     */
    public OneEntityConditionBuilder<T> havingField(SFunction<T, ?> sFunction) {
      String column = getColumnFromSFunction(sFunction);
      super.havingField("t0", column);
      return this;
    }

    /**
     * 支持lambda表达式指定orderbyAsc条件
     * @param sFunction
     * @return
     */
    public OneEntityConditionBuilder<T> orderbyAsc(SFunction<T, ?> sFunction) {
      String column = getColumnFromSFunction(sFunction);
      super.orderbyAsc("t0", column);
      return this;
    }

    /**
     * 支持lambda表达式指定orderByDesc条件
     * @param sFunction
     * @return
     */
    public OneEntityConditionBuilder<T> orderByDesc(SFunction<T, ?> sFunction) {
      String column = getColumnFromSFunction(sFunction);
      super.orderByDesc("t0", column);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> distinct(Boolean distinct) {
      super.distinct(distinct);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> innerJoin(String target, String alias) {
      super.innerJoin(target, alias);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> leftJoin(String target, String alias) {
      super.leftJoin(target, alias);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> rightJoin(String target, String alias) {
      super.rightJoin(target, alias);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> on(String sourceAlias1, String field1, String sourceAlias2,
        String field2) {
      super.on(sourceAlias1, field1, sourceAlias2, field2);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> where() {
      super.where();
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> andStart() {
      super.andStart();
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> andEnd() {
      super.andEnd();
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> orStart() {
      super.orStart();
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> orEnd() {
      super.orEnd();
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> field(String sourceAlias, String sourceField) {
      super.field(sourceAlias, sourceField);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> eq(String strValue) {
      super.eq(strValue);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> eq(String sourceAlias, String sourceField) {
      super.eq(sourceAlias, sourceField);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> notEq(String strValue) {
      super.notEq(strValue);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> notEq(String sourceAlias, String sourceField) {
      super.notEq(sourceAlias, sourceField);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> greater(String strValue) {
      super.greater(strValue);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> greater(String sourceAlias, String sourceField) {
      super.greater(sourceAlias, sourceField);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> less(String strValue) {
      super.less(strValue);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> less(String sourceAlias, String sourceField) {
      super.less(sourceAlias, sourceField);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> greaterEqual(String strValue) {
      super.greaterEqual(strValue);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> greaterEqual(String sourceAlias, String sourceField) {
      super.greaterEqual(sourceAlias, sourceField);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> lessEqual(String strValue) {
      super.lessEqual(strValue);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> lessEqual(String sourceAlias, String sourceField) {
      super.lessEqual(sourceAlias, sourceField);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> like(String strValue) {
      super.like(strValue);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> likeStart(String strValue) {
      super.likeStart(strValue);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> likeEnd(String strValue) {
      super.likeEnd(strValue);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> in(List list) {
      super.in(list);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> in(Collection list) {
      super.in(list);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> notIn(List list) {
      super.notIn(list);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> notIn(Collection list) {
      super.notIn(list);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> isNull() {
      super.isNull();
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> isNotNull() {
      super.isNotNull();
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> between(String strValue1, String strValue2) {
      super.between(strValue1, strValue2);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> groupby(String sourceAlias, String field) {
      super.groupby(sourceAlias, field);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> page(Integer pageIndex, Integer size) {
      super.page(pageIndex, size);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> limit(Integer offset, Integer size) {
      super.limit(offset, size);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> havingField(String sourceAlias, String sourceField) {
      super.havingField(sourceAlias, sourceField);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> havingOperator(OperatorEnum operatorEnum) {
      super.havingOperator(operatorEnum);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> havingStrValue(String strValue) {
      super.havingStrValue(strValue);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> havingStrValue(String strValue1, String strValue2) {
      super.havingStrValue(strValue1, strValue2);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> havingListValue(String strValue) {
      super.havingListValue(strValue);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> orderbyAsc(String sourceAlias, String sourceField) {
      super.orderbyAsc(sourceAlias, sourceField);
      return this;
    }

    @Override
    public OneEntityConditionBuilder<T> orderByDesc(String sourceAlias, String sourceField) {
      super.orderByDesc(sourceAlias, sourceField);
      return this;
    }

    @Override
    public EntityCondition build() {
      return super.build();
    }

  }

  /**
   * 从lambda表达式中解析出column名称并返回
   * TODO
   * @param sFunction
   * @return
   */
  private static String getColumnFromSFunction(SFunction sFunction) {
    SerializedLambda serializedLambda = LambdaUtils.resolve(sFunction);
    String fieldName = PropertyNamer.methodToProperty(serializedLambda.getImplMethodName());
    Class aClass = serializedLambda.getInstantiatedMethodType();
    Map<String, ColumnCache> columnCacheMap = LambdaUtils.getColumnMap(aClass);
    ColumnCache columnCache = columnCacheMap.get(LambdaUtils.formatKey(fieldName));
    Assert.notNull(columnCache, "can not find lambda cache for this property [%s] of entity [%s]",
        fieldName, aClass.getName());
    String column = columnCache.getColumn();
    return column;
  }

  public static class EntityConditionBuilder {

    protected Boolean distinct = false;
    protected List<Join> joins = new ArrayList<>();
    protected Conditions conditions;
    protected Groupby groupby;
    protected Having having;
    protected Orderby orderby;
    protected Limit limit;

    protected Join tmpJoin;
    protected On tmpOn;

    protected Condition tmpCondition = null;

    protected Stack<Condition> preConditionStack = new Stack<>();

    protected Stack<OperatorEnum> operatorEnumStack = new Stack<>();

    protected Condition tmpHavingCondition;

    protected EntityConditionBuilder() {
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

    void addCondition() {
      if (tmpCondition != null && preConditionStack.isEmpty()) {
        conditions.getConditionList().add(tmpCondition);
      } else if (tmpCondition != null) {
        preConditionStack.peek().getConditionList().add(tmpCondition);
      }
    }
  }
}
