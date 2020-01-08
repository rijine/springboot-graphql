package com.yonyou.einvoice.common.agile.service;

import com.yonyou.einvoice.common.agile.element.EntityCondition;
import com.yonyou.einvoice.common.agile.mp.repository.IExtendMetaMapper;
import com.yonyou.einvoice.common.agile.mp.repository.IMetaMapper;
import com.yonyou.einvoice.common.agile.visitor.ExtendConditionVisitor;
import com.yonyou.einvoice.common.agile.visitor.MybatisSqlVisitor;
import com.yonyou.einvoice.common.agile.visitor.SecureVisitor;
import graphql.language.Field;
import graphql.language.SelectionSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

public abstract class AbstractService<T, Q extends IMetaMapper> extends AbstractCommonService {

  @Autowired
  @SuppressWarnings("all")
  protected Q mapper;

  /**
   * 父表的column集合
   */
  protected Set<String> parentTableColumnSet;

  /**
   * 子表的column集合
   */
  protected Set<String> selfTableColumnSet;

  /**
   * 扩展类Mapper。如果存在扩展类，则mapper不为空。 如果不存在扩展类，则mapper为空。
   */
  protected IExtendMetaMapper extendMapper;

  /**
   * 根据传入的参数列表，返回需要在实体所对应的数据库表中实际查询的字段
   *
   * @param fieldList
   * @return
   */
  protected abstract List<String> getSelectFields(List<String> fieldList);


  /**
   * 用于根据GraphQL请求时传递的当前查询层次的field，获取实际需要在数据库中进行查询所需的字段列表
   *
   * @param field
   * @return
   */
  public List<String> getSelectFields(Field field) {
    SelectionSet selectionSet = field.getSelectionSet();
    if (selectionSet == null || CollectionUtils.isEmpty(selectionSet.getSelections())) {
      return Collections.emptyList();
    }
    List<String> selectFields = new ArrayList<>(selectionSet.getSelections().size());
    selectionSet.getSelections().forEach(selection -> {
      Field subField = (Field) selection;
      selectFields.add(subField.getName());
    });
    return getSelectFields(selectFields);
  }


  protected List<T> innerSelect(EntityCondition condition, List<String> selectFields) {
    Map<String, Object> map = new TreeMap<>();
    List<String> fields = new ArrayList<>();
    if (!CollectionUtils.isEmpty(selectFields) && extendMapper != null) {
      selectFields.forEach(selectField -> {
        String field = selectField.substring(1, selectField.length() - 1);
        if (parentTableColumnSet != null && parentTableColumnSet.contains(field)) {
          fields.add("t_s0." + selectField);
        }
        if (selfTableColumnSet != null && selfTableColumnSet.contains(field)) {
          fields.add("t_s1." + selectField);
        }
      });
      map.put("selectFields", fields);
    } else if (!CollectionUtils.isEmpty(selectFields)) {
      map.put("selectFields", selectFields);
    }
    if (condition.getDistinct()) {
      map.put("distinct", true);
    }
    // 检查是否存在sql注入情形
    MybatisSqlVisitor visitor = new MybatisSqlVisitor();
    visitor.visit(condition);
    // 针对本次查询所生成的sql
    map.put("conditionSql", visitor.getSql());
    // 本次查询生成的sql中，包含的mybatis变量。
    map.putAll(visitor.getMybatisParamMap());
    if (extendMapper != null) {
      return extendMapper.selectByDynamicCondition(map);
    }
    return ((IMetaMapper<T>) mapper).selectByDynamicCondition(map);
  }

  protected List<T> innerRelativeSelect(EntityCondition condition, List<String> relateFields) {
    Map<String, Object> map = new TreeMap<>();
    map.put("relateFields", relateFields);
    if (condition.getDistinct()) {
      map.put("distinct", true);
    }
    // 检查是否存在sql注入情形
    MybatisSqlVisitor visitor = new MybatisSqlVisitor();
    visitor.visit(condition);
    // 针对本次查询所生成的sql
    map.put("conditionSql", visitor.getSql());
    // 本次查询生成的sql中，包含的mybatis变量。
    map.putAll(visitor.getMybatisParamMap());
    if (extendMapper != null) {
      return extendMapper.selectByDynamicCondition(map);
    }
    return ((IMetaMapper<T>) mapper).selectByDynamicCondition(map);
  }


  protected void visitCondition(EntityCondition condition) {
    SecureVisitor secureVisitor = new SecureVisitor();
    secureVisitor.visit(condition);
    ExtendConditionVisitor visitor = new ExtendConditionVisitor();
    visitor.setParentColumnSet(parentTableColumnSet);
    visitor.setSelfColumnSet(selfTableColumnSet);
    visitor.visit(condition);
  }

  /**
   * 用于获取当前service对应实体的数据库表名
   *
   * @return
   */
  protected abstract String getTableName();
}
