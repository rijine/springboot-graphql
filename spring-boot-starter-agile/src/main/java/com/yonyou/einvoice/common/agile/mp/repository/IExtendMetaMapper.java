package com.yonyou.einvoice.common.agile.mp.repository;

import java.util.List;
import java.util.Map;

/**
 * 扩展元数据通用Mapper。用于对扩展元数据进行注入。 其中的实现方法和IMetaMapper、BaseMapper保持一致。
 *
 * @param <T>
 */
public interface IExtendMetaMapper<T> extends IMetaMapper<T> {

  /**
   * 查询条件动态拼接的mapper，可用于获取分页数据
   *
   * @param map
   * @return
   */
  @Override
  List<T> selectByDynamicCondition(Map<String, Object> map);

  /**
   * 查询条件动态拼接，用于分页情况下获取符合条件的记录总数
   *
   * @param map
   * @return
   */
  @Override
  int countAllByDynamicCondition(Map<String, Object> map);

  /**
   * 批量插入
   *
   * @param entityList
   * @return
   */
  @Override
  int insertBatchSomeColumn(List<T> entityList);
}
