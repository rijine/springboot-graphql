package com.yonyou.einvoice.common.agile.mp.repository;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ICommonEntityMapper {

  @Select("${sql}")
  <T> List<T> selectEntityOfType(Map<String, Object> map);
}
