package com.yonyou.einvoice.common.agile.service;

import com.yonyou.einvoice.common.agile.element.ISqlEntity;
import com.yonyou.einvoice.common.agile.mp.repository.ICommonEntityMapper;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CommonEntityDao {

  public static final ThreadLocal local = new ThreadLocal();

  @Autowired
  private ICommonEntityMapper entityMapper;

  public <T> List<T> selectEntityOfType(ISqlEntity sqlEntity, Class<T> clazz) {
    String sql = sqlEntity.getSql();
    Map<String, Object> paramMap = sqlEntity.getMybatisParamMap();
    paramMap.put("sql", sql);
    try {
      local.set(clazz);
      List<T> list = entityMapper.selectEntityOfType(paramMap);
      return list;
    } finally {
      local.remove();
    }
  }
}
