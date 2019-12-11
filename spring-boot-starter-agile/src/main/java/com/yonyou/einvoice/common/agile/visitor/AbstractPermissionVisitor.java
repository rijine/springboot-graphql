package com.yonyou.einvoice.common.agile.visitor;


import com.yonyou.einvoice.common.agile.element.Condition;
import com.yonyou.einvoice.common.agile.element.Conditions;
import com.yonyou.einvoice.common.agile.element.Entity;
import com.yonyou.einvoice.common.agile.element.Join;
import com.yonyou.einvoice.common.agile.element.Source;
import com.yonyou.einvoice.common.agile.service.IPermissionCommonService;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * 权限相关visitor
 *
 * @author liuqiangm
 */
public abstract class AbstractPermissionVisitor extends BaseVisitor {

  @Override
  public void visit(Source source) {
    /**
     * key为数据库表名，value为表字段 -> 表字段权限值映射
     */
    Map<String, Map<String, Object>> permissionConditionMap = getPermissionConditionMap();
    if (CollectionUtils.isEmpty(permissionConditionMap)) {
      return;
    }
    Entity entity = source.getEntity();
    // 用于判断conditions是否发生了变更。
    boolean[] flag = new boolean[]{false};
    Conditions conditions = source.getConditions();
    if (conditions == null) {
      conditions = new Conditions();
    }
    if (permissionConditionMap.containsKey(entity.getSource())) {
      addPermission(entity.getSource(), entity.getAlias(), conditions, flag,
          permissionConditionMap);
    }
    if (!CollectionUtils.isEmpty(entity.getJoins())) {
      for (Join join : entity.getJoins()) {
        addPermission(join.getTarget(), join.getAlias(), conditions, flag, permissionConditionMap);
      }
    }
    // conditions已经发生了变化，重新设置回source
    if (flag[0]) {
      source.setConditions(conditions);
    }
    // 继续在树的子节点上进行遍历、权限添加
    source.accept(this);
  }

  /**
   * 添加权限控制
   *
   * @param tableName
   * @param tableAlias
   * @param conditions
   * @param flag
   */
  private void addPermission(String tableName, String tableAlias, Conditions conditions,
      boolean[] flag, Map<String, Map<String, Object>> permissionConditionMap) {
    if (StringUtils.isEmpty(tableName) || !permissionConditionMap.containsKey(tableName)) {
      return;
    }
    Map<String, Object> objPairs = permissionConditionMap.get(tableName);
    IPermissionCommonService permissionCommonService = () -> {
      return objPairs;
    };
    List<Condition> authConditions = permissionCommonService.getAuthConditions();
    // 重新修正Entity别名
    authConditions.forEach(condition -> reviseTableAlias(condition, tableAlias));
    if (CollectionUtils.isEmpty(conditions.getAuthConditions())) {
      conditions.setAuthConditions(new LinkedList<>());
      flag[0] = true;
    }
    conditions.getAuthConditions().addAll(authConditions);
  }

  /**
   * 递归的修正每个Condition中的tableAlias
   *
   * @param condition
   * @param tableAlias
   */
  private void reviseTableAlias(Condition condition, String tableAlias) {
    if (condition == null) {
      return;
    }
    if (condition.getSourceField() != null) {
      condition.getSourceField().setSourceAlias(tableAlias);
    }
    if (!CollectionUtils.isEmpty(condition.getConditionList())) {
      condition.getConditionList().forEach(condition1 -> reviseTableAlias(condition1, tableAlias));
    }
  }

  /**
   * 子类中只需实现该方法，即可实现权限元素添加 key为数据库表名 value为该表所对应的的key->value映射。 其中，key为表字段名称，value为表字段的值。
   * value值可以是单独的值，如：String、int、double等，这种情况下，会进行等值判断。 也可以是list，这种情况下，在query语句或update、delete语句中，会使用in语句进行权限限定。
   * 在对数据进行insert插入时，只有value为单独的值的会回填到对应实体当中。 如果value为列表，则权限控制仅对select/update/delete语句起作用，对insert不起作用。
   * 如果value为单独的值，则权限控制对所有类型的sql语句都起作用。
   * <p>
   * 详见示例！
   *
   * @return
   */
  public abstract Map<String, Map<String, Object>> getPermissionConditionMap();

}
