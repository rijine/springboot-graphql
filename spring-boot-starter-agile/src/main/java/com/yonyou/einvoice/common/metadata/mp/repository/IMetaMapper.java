package com.yonyou.einvoice.common.metadata.mp.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import java.util.Map;

/**
 * 通用Mapper。 在mabatis-plus提供的基本方法之外，额外添加了三个mapper方法
 *
 * @param <T>
 */
public interface IMetaMapper<T> extends BaseMapper<T> {

  /**
   * 查询条件动态拼接的mapper，可用于获取分页数据
   *
   * @param map
   * @return
   */
  List<T> selectByDynamicCondition(Map<String, Object> map);

  /**
   * 查询条件动态拼接，用于分页情况下获取符合条件的记录总数
   *
   * @param map
   * @return
   */
  int countAllByDynamicCondition(Map<String, Object> map);

  /**
   * 批量插入
   *
   * @param entityList
   * @return
   */
  int insertBatchSomeColumn(List<T> entityList);

}
