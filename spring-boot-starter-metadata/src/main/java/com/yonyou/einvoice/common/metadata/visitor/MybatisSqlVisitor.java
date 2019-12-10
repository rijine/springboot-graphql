package com.yonyou.einvoice.common.metadata.visitor;

import com.yonyou.einvoice.common.metadata.element.Condition;
import com.yonyou.einvoice.common.metadata.element.EntityCondition;
import com.yonyou.einvoice.common.metadata.element.Field;
import com.yonyou.einvoice.common.metadata.element.Source;
import com.yonyou.einvoice.common.metadata.element.Value;
import com.yonyou.einvoice.common.metadata.enums.OperatorEnum;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.util.CollectionUtils;

/**
 * 用于生成mybatis的查询sql
 *
 * @author liuqiangm
 */
public class MybatisSqlVisitor extends BaseSqlVisitor {

  private Map<String, Object> paramMap = new HashMap<>();

  private int paramIndex = 1;

  @Override
  public void visit(Condition condition) {
    OperatorEnum operatorEnum = condition.getOperator();
    // 如果in语句的子list为空，则添加 1 <> 1（永远为false）。
    // 避免由于list为空或恶意构造的为空list，造成无权限数据泄露问题
    boolean listOperator =
        OperatorEnum.IN.equals(operatorEnum) || OperatorEnum.NOTIN.equals(operatorEnum);
    if (listOperator && condition.getV1() != null
        && condition.getV1().getSource() == null
        && CollectionUtils.isEmpty(condition.getV1().getList())) {
      append("1 <> 1");
      return;
    }
    processCondition(condition);
    processAndOrCondition(condition);
    if (OperatorEnum.BETWEEN.equals(operatorEnum)) {
      /**
       * between的两个v1、v2都不能为null，否则拼接为is null。
       */
      if (condition.getV1() != null && condition.getV1().getVal() == null
          && condition.getV1().getList() == null && condition.getV1().getSource() == null) {
        throw new RuntimeException("between语句的第一个参数不能为空！");
      }
      if (condition.getV2() != null && condition.getV2().getVal() == null
          && condition.getV2().getList() == null && condition.getV2().getSource() == null) {
        throw new RuntimeException("between语句的第二个参数不能为空！");
      }
      String v1 = getMybatisParam();
      String v2 = getMybatisParam();

      paramMap.put(v1, condition.getV1().getValue());
      paramMap.put(v2, condition.getV2().getValue());

      append(operatorEnum.getCode())
          .append(getWrappedMybatisParam(v1))
          .append("and")
          .append(getWrappedMybatisParam(v2));
      return;
    }
    boolean andOrOperator =
        OperatorEnum.AND.equals(operatorEnum) || OperatorEnum.OR.equals(operatorEnum);
    if (andOrOperator) {
      return;
    }
    boolean strOperator = OperatorEnum.EQUAL.equals(operatorEnum)
        || OperatorEnum.NOTEQUAL.equals(operatorEnum)
        || OperatorEnum.LESS.equals(operatorEnum)
        || OperatorEnum.LESSEQUAL.equals(operatorEnum)
        || OperatorEnum.GREATER.equals(operatorEnum)
        || OperatorEnum.GREATEREQUAL.equals(operatorEnum)
        || OperatorEnum.LIKE.equals(operatorEnum);
    if (strOperator && (condition.getV1() == null || condition.getV1().getVal() == null)
        && condition.getV1().getField() == null) {
      throw new RuntimeException("null值不能使用=、<>、!=、<、<=、>、>=、like操作符，请检查");
//      append(OperatorEnum.ISNULL.getCode());
//      return;
    }
    append(operatorEnum.getCode());
    if (condition.getV1() != null) {
      this.visit(condition.getV1());
    }
  }


  @Override
  public void visit(Value value) {
    Object val = value.getValue();
    if (val instanceof Source) {
      append("(");
      this.visit((Source) val);
      append(")");
      return;
    }
    if (val instanceof List) {
      appendListValue((List) val);
      return;
    }
    if (val instanceof Field) {
      this.visit((Field) val);
      return;
    }
    String param = getMybatisParam();
    append(getWrappedMybatisParam(param));
    paramMap.put(param, val);
  }

  private void appendListValue(List list) {
    if (CollectionUtils.isEmpty(list)) {
      return;
    }
    // 初始化StringBuilder，避免扩容可能造成的问题。依据：#{_p**},
    StringBuilder tmpBuilder = new StringBuilder(list.size() * 9 + 1);
    String param = getMybatisParam();
    tmpBuilder.append("(#{").append(param).append("}");
    Iterator iterator = list.iterator();
    Object value = iterator.next();
    paramMap.put(param, value);
    while (iterator.hasNext()) {
      param = getMybatisParam();
      value = iterator.next();
      tmpBuilder.append(",#{").append(param).append("}");
      paramMap.put(param, value);
    }
    tmpBuilder.append(")");
    append(tmpBuilder.toString());

  }

  /**
   * 获取mybatis的sql语句
   *
   * @return
   */
  @Override
  public String getSql() {
    int length = sqlBuilder.length();
    if (length > 0) {
      sqlBuilder.setLength(length - 1);
    }
    String result = sqlBuilder.toString();
    sqlBuilder.setLength(length);
    return result;
  }

  /**
   * 获取mybatis的paramMap
   *
   * @return
   */
  public Map<String, Object> getMybatisParamMap() {
    return this.paramMap;
  }

  /**
   * 将sqlBuilder长度重置
   */
  @Override
  public void reset() {
    this.sqlBuilder.setLength(0);
    this.paramMap.clear();
    this.paramIndex = 1;
  }

  @Override
  public void visit(EntityCondition sourceCondition) {
    super.visit(sourceCondition);
  }

  private String getWrappedMybatisParam(String var) {
    return String.format("#{%s}", var);
  }

  private String getMybatisParam() {
    return String.format("_p%d", paramIndex++);
  }

}
