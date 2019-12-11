package com.yonyou.einvoice.common.metadata.mp.methods;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 注入countAllDynamic
 *
 * @author liuqiangm
 */
public class CountAllDynamicMethod extends AbstractMethod {

  @Override
  public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass,
      TableInfo tableInfo) {
    String sql = "<script>    select count(1)\n"
        + "    from %s t0\n"
        + "    ${conditionSql}</script>";
    sql = String.format(sql, tableInfo.getTableName());
    SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, null);
    return this
        .addSelectMappedStatementForOther(mapperClass, "countAllByDynamicCondition", sqlSource,
            Integer.class);
  }
}
