package com.yonyou.einvoice.common.agile.mp.interceptor;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.type.TypeHandler;

@Setter
@Getter
@ToString
class ReflectEntity {

  private Field field;

  /**
   * 数据库表的column名称
   */
  private String columnName;

  /**
   * 实体的property名称
   */
  private String propertyName;

  /**
   * 实体的property的type类型
   */
  private Class propertyType;

  /**
   * 属性值赋值器
   */
  private BiConsumer<Object, Object> propertyValueAssigner;

  /**
   * aggField对象生成器
   */
  private Supplier aggFieldSupplier;

  private List<ReflectEntity> aggDetailReflectEntityList;

  /**
   * 实际执行的处理方法 用于对实体进行字段映射，以及子对象映射 若当前resultSet获取不到column相应的值，或该值为null，则返回false。 否则，返回true
   *
   * @param obj
   * @param resultSet
   * @return
   */
  public boolean process(Object obj, ResultSet resultSet) {
    if (aggFieldSupplier != null) {
      Object aggFieldObj = aggFieldSupplier.get();
      boolean hasSubAggFields = false;
      // 递归处理聚合字段
      for (ReflectEntity reflectEntity : aggDetailReflectEntityList) {
        hasSubAggFields |= reflectEntity.process(aggFieldObj, resultSet);
      }
      // 如果子字段有值，才生成子对象，并赋值给父对象
      if (hasSubAggFields) {
        propertyValueAssigner.accept(obj, aggFieldObj);
      }
      return true;
    }
    TypeHandler typeHandler = EntityInterceptor.typeHandlerRegistry.getTypeHandler(propertyType);
    try {
      // 获取columnName的值
      Object val = typeHandler.getResult(resultSet, columnName);
      if (val == null) {
        return false;
      }
      // 回填属性值
      propertyValueAssigner.accept(obj, val);
    } catch (Exception e) {
      // columnName在resultSet中不存在，抛异常也不用处理，直接忽略
      return false;
    }
    return true;
  }

  /**
   * 实际执行的处理方法 用于对实体进行字段映射，以及子对象映射 若当前resultSet获取不到column相应的值，或该值为null，则返回false。 否则，返回true
   *
   * @param obj
   * @param resultSet
   * @return
   */
  public boolean process(Object obj, ResultSet resultSet, Set<String> columnSet) {
    if (aggFieldSupplier != null) {
      Object aggFieldObj = aggFieldSupplier.get();
      boolean hasSubAggFields = false;
      // 递归处理聚合字段
      for (ReflectEntity reflectEntity : aggDetailReflectEntityList) {
        hasSubAggFields |= reflectEntity.process(aggFieldObj, resultSet, columnSet);
      }
      // 如果子字段有值，才生成子对象，并赋值给父对象
      if (hasSubAggFields) {
        propertyValueAssigner.accept(obj, aggFieldObj);
      }
      return true;
    }
    // 如果执行结果的column当中不包含当前columnName，则直接返回false
    if (!columnSet.contains(columnName)) {
      return false;
    }
    TypeHandler typeHandler = EntityInterceptor.typeHandlerRegistry.getTypeHandler(propertyType);
    try {
      // 获取columnName的值
      Object val = typeHandler.getResult(resultSet, columnName);
      if (val == null) {
        return false;
      }
      // 回填属性值
      propertyValueAssigner.accept(obj, val);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public ReflectEntity cloneReflectEntity() {
    ReflectEntity reflectEntity = new ReflectEntity();
    reflectEntity.setField(field);
    reflectEntity.setColumnName(columnName);
    reflectEntity.setPropertyName(propertyName);
    reflectEntity.setPropertyType(propertyType);
    reflectEntity.setPropertyValueAssigner(propertyValueAssigner);
    reflectEntity.setAggFieldSupplier(aggFieldSupplier);
    if (aggDetailReflectEntityList != null) {
      List<ReflectEntity> list = new ArrayList<>(aggDetailReflectEntityList.size());
      list.addAll(aggDetailReflectEntityList);
      reflectEntity.setAggDetailReflectEntityList(list);
    }
    return reflectEntity;
  }

}
