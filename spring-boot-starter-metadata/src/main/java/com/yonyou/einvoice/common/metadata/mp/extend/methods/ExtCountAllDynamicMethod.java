package com.yonyou.einvoice.common.metadata.mp.extend.methods;

import com.yonyou.einvoice.common.metadata.mp.extend.AbstractExtMethod;
import com.yonyou.einvoice.common.metadata.mp.extend.ExtTableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 注入countAllDynamic
 *
 * @author liuqiangm
 */
public class ExtCountAllDynamicMethod extends AbstractExtMethod {

  @Override
  public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass,
      ExtTableInfo tableInfo) {
    String sql = "<script>    select count(1)\n"
        + "    from %s t_s0 left join %s t_s1 on t_s0.%s = t_s1.%s\n"
        + "    ${conditionSql}</script>";
    sql = String.format(sql, tableInfo.getParentTableName(), tableInfo.getTableName(),
        tableInfo.getParentKeyColumn(), tableInfo.getKeyColumn());
    SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, null);
    return this
        .addSelectMappedStatementForOther(mapperClass, "countAllByDynamicCondition", sqlSource,
            Integer.class);
  }
}
