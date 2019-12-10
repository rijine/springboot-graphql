package com.yonyou.einvoice.common.metadata.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.interfaces.Compare;
import com.baomidou.mybatisplus.core.conditions.interfaces.Func;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yonyou.einvoice.common.metadata.element.Conditions;
import com.yonyou.einvoice.common.metadata.element.EntityCondition;
import com.yonyou.einvoice.common.metadata.mp.repository.IMetaMapper;
import com.yonyou.einvoice.common.metadata.visitor.AbstractPermissionVisitor;
import graphql.language.Field;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * 在基础service之上，封装权限service类
 *
 * @param <T> 实体类
 * @param <Q> 实体类对应的Mapper类
 * @author liuqiangm
 */
@Slf4j
public abstract class AbstractMybatisPermissionService<T, Q extends IMetaMapper> extends
    AbstractMybatisService<T, Q> implements
    IPermissionCommonService {


  /**
   * 重写父类方法。用于在查询条件动态拼接的基础之上，添加权限控制。
   *
   * @param condition
   * @param field
   * @return
   */
  @Override
  public List<T> selectByDynamicCondition(
      @GraphQLArgument(name = "condition") EntityCondition condition,
      @GraphQLEnvironment Field field) {

    addPermissionConditions(condition, getPermissionVisitorList(applicationContext));
    return super.selectByDynamicCondition(condition, field);
  }

  /**
   * 查询条件动态拼接。用于获取符合条件的记录（可分页）
   *
   * @param condition
   * @param fields
   * @return
   */
  @Override
  public List<T> selectByDynamicCondition(
      EntityCondition condition, List<String> fields) {

    addPermissionConditions(condition, getPermissionVisitorList(applicationContext));
    return super.selectByDynamicCondition(condition, fields);
  }

  /**
   * 重写父类方法。用于在查询条件动态拼接的基础之上，添加权限控制。
   *
   * @param condition
   * @return
   */
  @Override
  public int countAllByDynamicCondition(
      @GraphQLArgument(name = "condition") EntityCondition condition) {

    addPermissionConditions(condition, getPermissionVisitorList(applicationContext));
    return super.countAllByDynamicCondition(condition);
  }

  /**
   * 插入操作，需要添加访问权限字段的强制赋值。
   *
   * @param entity
   * @return
   */
  @Override
  public int insert(T entity) {
    if (entity == null) {
      return 0;
    }

    Map<String, Object> permissionPairArray = getPermissionPairArray();
    // 未设置权限参数，则直接插入
    if (permissionPairArray == null || permissionPairArray.size() == 0) {
      return super.insert(entity);
    }
    // 设置权限参数，则设置权限参数的值
    for (Map.Entry<String, Object> permissionPair : permissionPairArray.entrySet()) {
      addPermissionField2Entity(entity, permissionPair);
    }
    return super.insert(entity);
  }

  /**
   * 批量插入，针对插入列表中的每个对象的权限字段进行强制赋值
   *
   * @param entityList
   * @return
   */
  @Override
  public int insertBatchSomeColumn(List<T> entityList) {
    if (CollectionUtils.isEmpty(entityList)) {
      return 0;
    }

    Map<String, Object> permissionPairArray = getPermissionPairArray();
    // 未设置权限参数，直接插入
    if (permissionPairArray == null || permissionPairArray.size() == 0) {
      return super.insertBatchSomeColumn(entityList);
    }
    // 设置了权限参数，强制赋权限参数属性值
    entityList.forEach(entity -> {
      for (Map.Entry<String, Object> permissionPair : permissionPairArray.entrySet()) {
        addPermissionField2Entity(entity, permissionPair);
      }
    });
    return super.insertBatchSomeColumn(entityList);
  }

  /**
   * 根据id进行删除，需要保证删除的数据除了id一致，还要在该用户的权限范围之内
   *
   * @param id
   * @return
   */
  @Override
  public int deleteById(Serializable id) {
    deleteByIdCheck(id);
    // 添加权限控制参数
    Wrapper<T> queryWrapper = getQueryWrapper(null);
    String keyColumn = getKeyColumn();
    // 添加id参数
    ((Compare) queryWrapper).eq(keyColumn, id);
    return super.delete(queryWrapper);
  }

  /**
   * 根据key-value进行删除。 首先，将map转换到queryWrapper当中，然后添加权限，然后删除
   *
   * @param columnMap
   * @return
   */
  @Override
  public int deleteByMap(Map<String, Object> columnMap) {
    deleteByMapCheck(columnMap);
    // 生成带权限wrapper
    Wrapper<T> queryWrapper = getQueryWrapper(null);
    // 加入columnMap中的属性
    columnMap.entrySet()
        .forEach(entry -> ((Compare) queryWrapper).eq(entry.getKey(), entry.getValue()));
    return super.delete(queryWrapper);
  }

  @Override
  public int delete(Wrapper<T> wrapper) {
    deleteByWrapperCheck(wrapper);
    // 补充权限属性
    Wrapper<T> queryWrapper = getQueryWrapper(wrapper);
    return super.delete(queryWrapper);
  }

  @Override
  public int deleteBatchIds(Collection<? extends Serializable> idList) {
    deleteBatchIdsCheck(idList);
    if (CollectionUtils.isEmpty(idList)) {
      throw new RuntimeException("根据主键批量删除，则主键列表不能为空!");
    }

    // 补充权限属性
    Wrapper<T> queryWrapper = getQueryWrapper(null);
    String keyColumn = getKeyColumn();
    // 补充in语句
    ((Func) queryWrapper).in(keyColumn, idList);
    return super.delete(queryWrapper);
  }

  /**
   * 根据id和权限进行更新
   *
   * @param entity
   * @return
   */
  @Override
  public int updateById(T entity) {

    Wrapper updateWrapper = getUpdateWrapper(null);
    String keyColumn = getKeyColumn();
    Object keyValue = getKeyPropertyValueOfEntity(entity);
    ((Compare) updateWrapper).eq(keyColumn, keyValue);
    return super.update(entity, updateWrapper);
  }

  /**
   * 添加权限进行更新
   *
   * @param entity
   * @param updateWrapper
   * @return
   */
  @Override
  public int update(T entity, Wrapper<T> updateWrapper) {

    Wrapper<T> wrapper = getUpdateWrapper(updateWrapper);
    return super.update(entity, wrapper);
  }

  /**
   * 添加权限进行查询
   *
   * @param id
   * @return
   */
  @Override
  public T selectById(Serializable id) {

    Wrapper<T> queryWrapper = getQueryWrapper(null);
    String keyColumn = getKeyColumn();
    ((Compare) queryWrapper).eq(keyColumn, id);
    return super.selectOne(queryWrapper);
  }

  /**
   * 添加权限进行批量查询
   *
   * @param idList
   * @return
   */
  @Override
  public List<T> selectBatchIds(Collection<? extends Serializable> idList) {

    // id列表为空，则返回空列表。
    if (CollectionUtils.isEmpty(idList)) {
      return Collections.emptyList();
    }
    // 添加权限控制
    Wrapper<T> queryWrapper = getQueryWrapper(null);
    String keyColumn = getKeyColumn();
    ((Func) queryWrapper).in(keyColumn, idList);
    return super.selectList(queryWrapper);
  }

  /**
   * 根据key -> value映射进行权限查询
   *
   * @param columnMap
   * @return
   */
  @Override
  public List<T> selectByMap(Map<String, Object> columnMap) {

    Wrapper<T> queryWrapper = getQueryWrapper(null);
    columnMap.entrySet()
        .forEach(entry -> ((Compare) queryWrapper).eq(entry.getKey(), entry.getValue()));
    return super.selectList(queryWrapper);
  }

  /**
   * 添加权限查询
   *
   * @param queryWrapper
   * @return
   */
  @Override
  public T selectOne(Wrapper<T> queryWrapper) {

    Wrapper<T> wrapper = getQueryWrapper(queryWrapper);
    return super.selectOne(wrapper);
  }

  /**
   * 添加权限查询数量
   *
   * @param queryWrapper
   * @return
   */
  @Override
  public Integer selectCount(Wrapper<T> queryWrapper) {

    Wrapper<T> wrapper = getQueryWrapper(queryWrapper);
    return super.selectCount(wrapper);
  }

  /**
   * 添加权限查询列表
   *
   * @param queryWrapper
   * @return
   */
  @Override
  public List<T> selectList(Wrapper<T> queryWrapper) {

    Wrapper<T> wrapper = getQueryWrapper(queryWrapper);
    return super.selectList(wrapper);
  }

  /**
   * 添加权限查询
   *
   * @param queryWrapper
   * @return
   */
  @Override
  public List<Map<String, Object>> selectMaps(Wrapper<T> queryWrapper) {

    Wrapper<T> wrapper = getQueryWrapper(queryWrapper);
    return super.selectMaps(wrapper);
  }

  /**
   * 添加权限查询objs
   *
   * @param queryWrapper
   * @return
   */
  @Override
  public List<Object> selectObjs(Wrapper<T> queryWrapper) {

    Wrapper<T> wrapper = getQueryWrapper(queryWrapper);
    return super.selectObjs(wrapper);
  }

  /**
   * 添加权限查询分页
   *
   * @param page
   * @param queryWrapper
   * @return
   */
  @Override
  public IPage<T> selectPage(IPage<T> page, Wrapper<T> queryWrapper) {

    Wrapper<T> wrapper = getQueryWrapper(queryWrapper);
    return super.selectPage(page, wrapper);
  }

  /**
   * 添加权限查询分页
   *
   * @param page
   * @param queryWrapper
   * @return
   */
  @Override
  public IPage<Map<String, Object>> selectMapsPage(IPage<T> page, Wrapper<T> queryWrapper) {

    Wrapper<T> wrapper = getQueryWrapper(queryWrapper);
    return super.selectMapsPage(page, wrapper);
  }

  /**
   * 针对T类型的对象entity，使用权限key->value键值对强制权限字段赋值。
   *
   * @param entity
   * @param permissionPair
   */
  private void addPermissionField2Entity(T entity, Map.Entry<String, Object> permissionPair) {
    if (permissionPair == null) {
      return;
    }
    // 获取权限的java bean属性字段
    String property = this.getPropertyOfColumn(permissionPair.getKey());
    if (StringUtils.isEmpty(property)) {
      throw new RuntimeException("权限属性设置有误，请检查权限属性设置是否正确: " + permissionPair.getKey());
    }
    if (!fieldMap.containsKey(property)) {
      // 将扩展类的Field添加进来

      // 将被扩展类的Field补充进来
      addField(entity.getClass(), property);
    }
    if ((permissionPair.getValue() instanceof Collection)
        || (permissionPair.getValue() instanceof EntityCondition)) {
      return;
    }
    try {
      fieldMap.get(property).set(entity, permissionPair.getValue());
    } catch (IllegalAccessException e) {
      log.error("", e);
      throw new RuntimeException(e);
    }
  }

  /**
   * 获取实体entity中的主键值
   *
   * @param entity
   * @return
   */
  private Object getKeyPropertyValueOfEntity(T entity) {
    String keyProperty = getKeyProperty();
    if (!fieldMap.containsKey(keyProperty)) {
      addField(entity.getClass(), keyProperty);
    }
    try {
      return fieldMap.get(keyProperty).get(entity);
    } catch (IllegalAccessException e) {
      log.error("", e);
      throw new RuntimeException(e);
    }
  }

  /**
   * 使用java反射，获取clazz对象的property属性的Field，并添加到fieldMap中缓存
   *
   * @param clazz
   * @param property
   */
  private void addField(Class clazz, String property) {
    try {
      java.lang.reflect.Field field = clazz.getDeclaredField(property);
      field.setAccessible(true);
      fieldMap.put(property, field);
    } catch (NoSuchFieldException e) {
      log.error("", e);
      throw new RuntimeException(e);
    }
  }


  /**
   * 根据源wrapper，查找权限参数，拼接目标queryWrapper
   *
   * @param wrapper
   * @return
   */
  private Wrapper<T> getQueryWrapper(Wrapper<T> wrapper) {
    Wrapper<T> queryWrapper = null;
    LambdaQueryWrapper lambdaQueryWrapper;
    if (wrapper != null) {
      queryWrapper = wrapper;
    } else {
      queryWrapper = new QueryWrapper<>();
    }
    addPermission2Wrapper(queryWrapper);
    return queryWrapper;
  }

  /**
   * 根据源wrapper，查找权限参数，拼接目标updateWrapper
   *
   * @param wrapper
   * @return
   */
  private Wrapper<T> getUpdateWrapper(Wrapper<T> wrapper) {
    Wrapper<T> updateWrapper = null;
    if (wrapper != null) {
      updateWrapper = wrapper;
    } else {
      updateWrapper = new UpdateWrapper<>();
    }
    addPermission2Wrapper(updateWrapper);
    return updateWrapper;
  }


  /**
   * 对mybatis-plus查询所使用的wrapper进行包装，添加权限控制条件
   *
   * @param wrapper
   */
  private void addPermission2Wrapper(Wrapper<T> wrapper) {
    Map<String, Object> permissionPairArray = getPermissionPairArray();
    if (permissionPairArray == null || permissionPairArray.size() == 0) {
      log.error("当前未设置用户权限参数，建议请检查是否符合预期！");
      return;
    }
    for (Map.Entry<String, Object> permissionPair : permissionPairArray.entrySet()) {
      if (permissionPair.getValue() instanceof List && wrapper instanceof Func) {
        ((Func) wrapper).in(permissionPair.getKey(), (List) permissionPair.getValue());
      } else if (permissionPair.getValue() != null && !(permissionPair
          .getValue() instanceof Conditions)
          && wrapper instanceof Compare) {
        ((Compare) wrapper).eq(permissionPair.getKey(), permissionPair.getValue());
      } else if (permissionPair.getKey() != null && permissionPair.getValue() instanceof Conditions
          && wrapper instanceof Compare) {
        throw new RuntimeException("权限查询条件暂时不支持传递Conditions类型");
      } else if (wrapper instanceof Func) {
        ((Func) wrapper).isNull(permissionPair.getKey());
      }
    }
  }

  /**
   * 获取权限相关的配置属性
   *
   * @return
   */
  @Override
  public Map<String, Object> getPermissionPairArray() {
    Collection<AbstractPermissionVisitor> permissionVisitors = getPermissionVisitorList(
        applicationContext);
    List<Map<String, Object>> list = new ArrayList<>();
    if (CollectionUtils.isEmpty(permissionVisitors)) {
      return Collections.emptyMap();
    }
    String tableName = getTableName();
    String parentTableName = getParentTableName();
    for (AbstractPermissionVisitor permissionVisitor : permissionVisitors) {
      Map<String, Map<String, Object>> permissionConditionMap = permissionVisitor
          .getPermissionConditionMap();
      addPermissionOfTable(tableName, permissionConditionMap, list);
      // 添加对扩展表的权限扩展
      addPermissionOfTable(parentTableName, permissionConditionMap, list);
    }
    Map<String, Object> resultMap = new HashMap<>(
        (int) ((list.stream().mapToInt(Map::size).sum()) / 0.75) + 1);
    list.forEach(tmpMap -> resultMap.putAll(tmpMap));
    return resultMap;
  }

  /**
   * 对表：tableName添加权限
   *
   * @param tableName
   * @param permissionConditionMap
   * @param list
   */
  private void addPermissionOfTable(String tableName,
      Map<String, Map<String, Object>> permissionConditionMap, List<Map<String, Object>> list) {
    if (tableName != null && permissionConditionMap != null && permissionConditionMap
        .containsKey(tableName)) {
      Map<String, Object> tmpPairArray = permissionConditionMap.get(tableName);
      if (tmpPairArray == null) {
        return;
      }
      list.add(tmpPairArray);
    }
  }
}
