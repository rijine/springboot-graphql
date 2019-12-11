package com.yonyou.einvoice.common.agile.service;

import com.yonyou.einvoice.common.agile.element.Condition;
import com.yonyou.einvoice.common.agile.element.Conditions;
import com.yonyou.einvoice.common.agile.element.EntityCondition;
import com.yonyou.einvoice.common.agile.element.Value;
import com.yonyou.einvoice.common.agile.enums.OperatorEnum;
import com.yonyou.einvoice.common.agile.visitor.AbstractPermissionVisitor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 访问权限控制的接口，部分方法添加了默认实现
 */
@FunctionalInterface
public interface IPermissionCommonService {

  /**
   * 获取authConditions。 该Condition列表将被添加到Conditions对象的authConditions属性，进而实现权限认证
   *
   * @return
   */
  default List<Condition> getAuthConditions() {
    Map<String, Object> permissionPairArray = getPermissionPairArray();
    if (permissionPairArray == null || permissionPairArray.size() == 0) {
      return null;
    }
    List<Condition> resultList = new ArrayList<>(permissionPairArray.size());
    for (Map.Entry<String, Object> permissionPair : permissionPairArray.entrySet()) {
      Condition condition = new Condition();
      com.yonyou.einvoice.common.agile.element.Field field = new com.yonyou.einvoice.common.agile.element.Field();
      // 首先默认设置为t0。在调用处根据entity的别名情况自行修改。
      // 若是EntityCondition中使用，则无需修改（默认为"t0"）
      field.setSourceAlias("t0");
      field.setField(permissionPair.getKey());
      condition.setSourceField(field);
      condition.setOperator(OperatorEnum.EQUAL);
      Value value = new Value();
      // 如果value是List类型
      if (permissionPair.getValue() instanceof List) {
        // 如果是列表类型，则使用IN操作
        condition.setOperator(OperatorEnum.IN);
        value.setList((List) permissionPair.getValue());
      } else if (permissionPair.getValue() instanceof Conditions) {
        // 如果value是Conditions类型，则直接拼接进去
        resultList.addAll(((Conditions) permissionPair.getValue()).getConditionList());
        continue;
      } else if (permissionPair.getValue() != null) {
        // 否则，将value设置为val。
        value.setVal(permissionPair.getValue().toString());
      } else {
        // 否则的话，拼接为is null
        condition.setOperator(OperatorEnum.ISNULL);
        resultList.add(condition);
        return resultList;
      }
      condition.setV1(value);
      resultList.add(condition);
    }
    return resultList;
  }

  /**
   * 用于添加访问权限的Conditions
   *
   * @param condition
   * @param permissionVisitors
   */
  default void addPermissionConditions(EntityCondition condition,
      Collection<AbstractPermissionVisitor> permissionVisitors) {
    Conditions conditions = condition.getConditions();
    if (conditions == null) {
      conditions = new Conditions();
    }
    conditions.setAuthConditions(getAuthConditions());
    condition.setConditions(conditions);
    permissionVisitors.forEach(permissionVisitor -> permissionVisitor.visit(condition));
  }

  /**
   * 用于添加权限相关的条件。 [0]为权限相关的key（数据库字段名）。 [1]为权限相关的value。 可以根据多组字段添加查询条件
   *
   * @return
   */
  Map<String, Object> getPermissionPairArray();
}
