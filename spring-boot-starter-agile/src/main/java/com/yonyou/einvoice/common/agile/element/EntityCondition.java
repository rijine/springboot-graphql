package com.yonyou.einvoice.common.agile.element;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.google.common.base.CaseFormat;
import com.yonyou.einvoice.common.agile.enums.DirectionEnum;
import com.yonyou.einvoice.common.agile.enums.JointypeEnum;
import com.yonyou.einvoice.common.agile.enums.OperatorEnum;
import com.yonyou.einvoice.common.agile.visitor.IMetaElement;
import com.yonyou.einvoice.common.agile.visitor.IVisitor;
import io.leangen.graphql.annotations.GraphQLIgnore;
import io.leangen.graphql.annotations.types.GraphQLType;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class EntityCondition implements IMetaElement {

  /**
   * 用于保存SerializedLambda的方法签名与Class映射关系
   */
  private static final Map<String, Class> instantiateMethodType2ClassMap = new ConcurrentHashMap<>();

  /**
   * 用于保存className-field与相应的数据库字段的映射关系
   */
  private static final Map<String, String> classField2ColumnMap = new ConcurrentHashMap<>();

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
  protected static EntityConditionBuilder builder() {
    return new EntityConditionBuilder();
  }

  @GraphQLIgnore
  public static <T> OneEntityConditionBuilder<T> oneEntityConditionBuilder(
      Class<T> clazz) {
    return new OneEntityConditionBuilder<T>();
  }

  @GraphQLIgnore
  public static <T, U> TwoEntityConditionBuilder<T, U> twoEntityConditionBuilder(
      Class<T> tClazz, Class<U> uClass) {
    return new TwoEntityConditionBuilder<T, U>(tClazz, uClass);
  }

  @GraphQLIgnore
  public static <T, U, S> ThreeEntityConditionBuilder threeEntityConditionBuilder(
      Class<T> tClass, Class<U> uClass, Class<S> sClass) {
    return new ThreeEntityConditionBuilder(tClass, uClass, sClass);
  }


  /**
   * 从lambda表达式中解析出column名称并返回
   *
   * @param sFunction
   * @return
   */
  public static String getColumnFromSFunction(SFunction sFunction) {
    SerializedLambda serializedLambda = getSerializedLambdaFromSFunction(sFunction);
    Class aClass = getImplClassFromSerializedLambda(serializedLambda);
    return getColumnFromClass(aClass, serializedLambda);
  }

  protected static String getColumnFromClass(Class clazz, SerializedLambda serializedLambda) {
    String fieldName = PropertyNamer.methodToProperty(serializedLambda.getImplMethodName());
    String classFieldName = clazz.getName() + "-" + fieldName;
    // 缓存类中字段所对应的column名称
    if (classField2ColumnMap.containsKey(classFieldName)) {
      return classField2ColumnMap.get(classFieldName);
    }
    Map<String, ColumnCache> columnCacheMap = LambdaUtils.getColumnMap(clazz);
    ColumnCache columnCache = columnCacheMap.get(LambdaUtils.formatKey(fieldName));
    String column = null;
    if (columnCache == null) {
      log.error(String.format("can not find lambda cache for this property [%s] of entity [%s]",
          fieldName, clazz.getName()));
      try {
        java.lang.reflect.Field field = getFieldOfName(fieldName, clazz);
        field.setAccessible(true);
        TableField tableField = field.getAnnotation(TableField.class);
        if (tableField != null && !StringUtils.isEmpty(tableField.value())) {
          column = tableField.value();
        } else {
          column = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
        }
      } catch (RuntimeException e) {
        log.error(e.getMessage(), e);
        column = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
      }
    } else {
      column = columnCache.getColumn();
    }
    classField2ColumnMap.put(classFieldName, column);
    return column;
  }

  /**
   * 获取class类中的fieldName字段对应的Field，包含从父类中查找
   *
   * @param fieldName
   * @param clazz
   * @return
   */
  private static java.lang.reflect.Field getFieldOfName(String fieldName, Class clazz) {
    try {
      java.lang.reflect.Field field = clazz.getDeclaredField(fieldName);
      return field;
    } catch (NoSuchFieldException e) {
      Class superClass = clazz.getSuperclass();
      if (java.util.Objects.equals(superClass, Object.class)) {
        throw new RuntimeException("class类中找不到" + fieldName + "字段");
      }
      return getFieldOfName(fieldName, superClass);
    }
  }

  protected static Class getImplClassFromSerializedLambda(SerializedLambda serializedLambda) {
    String instantiatedMethodType = serializedLambda.getInstantiatedMethodType();
    /**
     * 加入缓存，用于下次直接查找某lambda的get方法签名所在的class类
     */
    if (instantiateMethodType2ClassMap.containsKey(instantiatedMethodType)) {
      return instantiateMethodType2ClassMap.get(instantiatedMethodType);
    }
    int start = instantiatedMethodType.indexOf("(L");
    int end = instantiatedMethodType.indexOf(";)");
    String className = instantiatedMethodType.substring(start + 2, end);
    className = className.replaceAll("/", ".").replaceAll("\\\\", ".");
    Class aClass = null;
    try {
      aClass = Class.forName(className);
      instantiateMethodType2ClassMap.put(instantiatedMethodType, aClass);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return aClass;
  }

  protected static SerializedLambda getSerializedLambdaFromSFunction(SFunction function) {
    SerializedLambda serializedLambda = null;
    try {
      Method method = function.getClass().getDeclaredMethod("writeReplace");
      method.setAccessible(Boolean.TRUE);
      serializedLambda = (SerializedLambda) method.invoke(function);
      return serializedLambda;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
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

    protected EntityConditionBuilder innerJoin(String target, String alias) {
      tmpJoin = new Join();
      tmpJoin.setJointype(JointypeEnum.INNERJOIN);
      tmpJoin.setTarget(target);
      tmpJoin.setAlias(alias);
      return this;
    }

    protected EntityConditionBuilder leftJoin(String target, String alias) {
      tmpJoin = new Join();
      tmpJoin.setJointype(JointypeEnum.LEFTJOIN);
      tmpJoin.setTarget(target);
      tmpJoin.setAlias(alias);
      return this;
    }

    protected EntityConditionBuilder rightJoin(String target, String alias) {
      tmpJoin = new Join();
      tmpJoin.setJointype(JointypeEnum.RIGHTJOIN);
      tmpJoin.setTarget(target);
      tmpJoin.setAlias(alias);
      return this;
    }

    protected EntityConditionBuilder on(String sourceAlias1, String field1, String sourceAlias2,
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
    protected EntityConditionBuilder where() {
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

    protected EntityConditionBuilder eq(String sourceAlias, String sourceField) {
      tmpCondition.setOperator(OperatorEnum.EQUAL);
      this.fieldValue(sourceAlias, sourceField);
      return this;
    }

    protected EntityConditionBuilder notEq(String strValue) {
      tmpCondition.setOperator(OperatorEnum.NOTEQUAL);
      this.strValue(strValue);
      return this;
    }

    protected EntityConditionBuilder notEq(String sourceAlias, String sourceField) {
      tmpCondition.setOperator(OperatorEnum.NOTEQUAL);
      this.fieldValue(sourceAlias, sourceField);
      return this;
    }

    protected EntityConditionBuilder greater(String strValue) {
      tmpCondition.setOperator(OperatorEnum.GREATER);
      this.strValue(strValue);
      return this;
    }

    protected EntityConditionBuilder greater(String sourceAlias, String sourceField) {
      tmpCondition.setOperator(OperatorEnum.GREATER);
      this.fieldValue(sourceAlias, sourceField);
      return this;
    }

    protected EntityConditionBuilder less(String strValue) {
      tmpCondition.setOperator(OperatorEnum.LESS);
      this.strValue(strValue);
      return this;
    }

    protected EntityConditionBuilder less(String sourceAlias, String sourceField) {
      tmpCondition.setOperator(OperatorEnum.LESS);
      this.fieldValue(sourceAlias, sourceField);
      return this;
    }

    protected EntityConditionBuilder greaterEqual(String strValue) {
      tmpCondition.setOperator(OperatorEnum.GREATEREQUAL);
      this.strValue(strValue);
      return this;
    }

    protected EntityConditionBuilder greaterEqual(String sourceAlias, String sourceField) {
      tmpCondition.setOperator(OperatorEnum.GREATEREQUAL);
      this.fieldValue(sourceAlias, sourceField);
      return this;
    }

    protected EntityConditionBuilder lessEqual(String strValue) {
      tmpCondition.setOperator(OperatorEnum.LESSEQUAL);
      this.strValue(strValue);
      return this;
    }

    protected EntityConditionBuilder lessEqual(String sourceAlias, String sourceField) {
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

    protected EntityConditionBuilder in(List list) {
      tmpCondition.setOperator(OperatorEnum.IN);
      this.listValue(list);
      return this;
    }

    protected EntityConditionBuilder in(Collection list) {
      tmpCondition.setOperator(OperatorEnum.IN);
      if (list == null) {
        this.listValue(null);
        return this;
      }
      this.listValue(Arrays.asList(list.toArray()));
      return this;
    }

    protected EntityConditionBuilder notIn(List list) {
      tmpCondition.setOperator(OperatorEnum.NOTIN);
      this.listValue(list);
      return this;
    }

    protected EntityConditionBuilder notIn(Collection list) {
      tmpCondition.setOperator(OperatorEnum.NOTIN);
      if (list == null) {
        this.listValue(null);
        return this;
      }
      this.listValue(Arrays.asList(list.toArray()));
      return this;
    }

    protected EntityConditionBuilder isNull() {
      tmpCondition.setOperator(OperatorEnum.ISNULL);
      return this;
    }

    protected EntityConditionBuilder isNotNull() {
      tmpCondition.setOperator(OperatorEnum.ISNOTNULL);
      return this;
    }

    protected EntityConditionBuilder between(String strValue1, String strValue2) {
      tmpCondition.setOperator(OperatorEnum.BETWEEN);
      this.strValue(strValue1, strValue2);
      return this;
    }

    /**
     * group by语句构造
     *
     * @param sourceAlias
     * @param field
     * @return
     */
    protected EntityConditionBuilder groupby(String sourceAlias, String field) {
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
    protected EntityConditionBuilder page(Integer pageIndex, Integer size) {
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
    protected EntityConditionBuilder limit(Integer offset, Integer size) {
      limit = new Limit();
      limit.setOffset(offset);
      limit.setSize(size);
      return this;
    }

    protected EntityConditionBuilder havingField(String sourceAlias, String sourceField) {
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

    protected EntityConditionBuilder havingOperator(OperatorEnum operatorEnum) {
      tmpHavingCondition.setOperator(operatorEnum);
      return this;
    }

    protected EntityConditionBuilder havingStrValue(String strValue) {
      Value value = new Value();
      value.setVal(strValue);
      tmpHavingCondition.setV1(value);
      return this;
    }

    protected EntityConditionBuilder havingStrValue(String strValue1, String strValue2) {
      Value val1 = new Value();
      Value val2 = new Value();
      val1.setVal(strValue1);
      val2.setVal(strValue2);
      tmpHavingCondition.setV1(val1);
      tmpHavingCondition.setV2(val2);
      return this;
    }

    protected EntityConditionBuilder havingListValue(String strValue) {
      Value value = new Value();
      value.setVal(strValue);
      tmpHavingCondition.setV1(value);
      return this;
    }

    /**
     * orderby语句
     */
    protected EntityConditionBuilder orderbyAsc(String sourceAlias, String sourceField) {
      return orderbyAsc(sourceAlias, sourceField, DirectionEnum.ASC);
    }

    protected EntityConditionBuilder orderByDesc(String sourceAlias, String sourceField) {
      return orderbyAsc(sourceAlias, sourceField, DirectionEnum.DESC);
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

  public static <T> String getTableName(Class<T> tClass) {
    TableName table = tClass.getAnnotation(TableName.class);
    if (table != null && !StringUtils.isEmpty(table.value())) {
      return table.value();
    }
    String tableName = CaseFormat.UPPER_CAMEL
        .to(CaseFormat.LOWER_UNDERSCORE, tClass.getSimpleName());
    return tableName;
  }
}
