package com.yonyou.einvoice.common.metadata.visitor;

import com.yonyou.einvoice.common.metadata.element.Condition;
import com.yonyou.einvoice.common.metadata.element.Source;
import com.yonyou.einvoice.common.metadata.element.Value;
import com.yonyou.einvoice.common.metadata.enums.OperatorEnum;
import com.yonyou.einvoice.common.metadata.enums.TypeEnum;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用于生成可直接执行的sql
 *
 * @author liuqiangm
 */
public class RawSqlVisitor extends BaseSqlVisitor {

  @Override
  public void visit(Condition condition) {
    processCondition(condition);
    OperatorEnum operatorEnum = condition.getOperator();
    if (OperatorEnum.BETWEEN.equals(operatorEnum)) {
      append(operatorEnum.getCode())
          .append(getStrValue(condition.getV1().getValue()))
          .append("and")
          .append(getStrValue(condition.getV2().getValue()));
      return;
    }
    boolean andOrOperator =
        OperatorEnum.AND.equals(operatorEnum) || OperatorEnum.OR.equals(operatorEnum);
    if (andOrOperator) {
      return;
    }
    append(operatorEnum.getCode());
    if (condition.getV1() != null) {
      this.visit(condition.getV1());
    }
  }

  @Override
  public void visit(Value value) {
    if (value.getValue() instanceof Source) {
      append("(");
      this.visit((Source) value.getValue());
      append(")");
      return;
    }
    if (value.getValue() instanceof List) {
      processListValue(value);
      return;
    }
    processSingleValue(value);

  }

  private void processSingleValue(Value value) {
    String conditionStr = getStrValue(value.getValue());
    append(conditionStr);
  }

  /**
   * 处理列表值。将单个值加入到字符串拼接当中
   *
   * @param value
   * @author liuqiangm
   */
  private void processListValue(Value value) {
    if (!TypeEnum.LIST.equals(value.getType())) {
      throw new RuntimeException("当前condition只能传入非空的list类型对象");
    }
    String conditionStr = ((List<Object>) value.getValue()).stream()
        .map(obj -> getStrValue(obj)).collect(
            Collectors.joining(", ", "(", ")"));
    append(conditionStr);
  }

  private String getStrValue(Object value) {
    String str = String.format("'%s'", value.toString());
    return str;
  }

  /**
   * 将sqlBuilder长度重置
   */
  @Override
  public void reset() {
    this.sqlBuilder.setLength(0);
  }


}
