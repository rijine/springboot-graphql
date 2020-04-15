package com.yonyou.einvoice.common.agile.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.yonyou.einvoice.common.agile.element.EntityCondition;
import graphql.language.Field;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public interface IMybatisService<T> {

  List<String> getSelectFields(Field field);

  <T, R> List<List<T>> getSubFieldListOfList(List<T> tList, Function<T, R> function,
      Stream<R> stream);

  <T, R> List<T> getSubFieldList(List<T> tList, Function<T, R> function, Stream<R> stream);

  int insert(T entity);

  int deleteById(Serializable id);

  int deleteByMap(Map<String, Object> columnMap);

  int delete(Wrapper<T> wrapper);

  int deleteBatchIds(Collection<? extends Serializable> idList);

  int updateById(T entity);

  int update(T entity, Wrapper<T> updateWrapper);

  T selectById(Serializable id);

  List<T> selectBatchIds(Collection<? extends Serializable> idList);

  List<T> selectByMap(Map<String, Object> columnMap);

  T selectOne(Wrapper<T> queryWrapper);

  Integer selectCount(Wrapper<T> queryWrapper);

  List<T> selectList(Wrapper<T> queryWrapper);

  List<Map<String, Object>> selectMaps(Wrapper<T> queryWrapper);

  List<Object> selectObjs(Wrapper<T> queryWrapper);

  IPage<T> selectPage(IPage<T> page, Wrapper<T> queryWrapper);

  IPage<Map<String, Object>> selectMapsPage(IPage<T> page,
      Wrapper<T> queryWrapper);

  int insertBatchSomeColumn(List<T> entityList);

  List<T> selectByDynamicCondition(EntityCondition condition, Field field);

  List<T> selectByDynamicCondition(EntityCondition condition, SFunction<T, ?>... sFunctions);

  int countAllByDynamicCondition(EntityCondition condition);
}
