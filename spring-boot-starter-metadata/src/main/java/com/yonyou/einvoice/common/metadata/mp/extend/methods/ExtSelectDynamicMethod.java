package com.yonyou.einvoice.common.metadata.mp.extend.methods;

import com.yonyou.einvoice.common.metadata.mp.extend.AbstractExtMethod;
import com.yonyou.einvoice.common.metadata.mp.extend.ExtTableInfo;
import java.util.Map;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 注入selectDynamic
 *
 * @author liuqiangm
 */
public class ExtSelectDynamicMethod extends AbstractExtMethod {

  @Override
  public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass,
      ExtTableInfo tableInfo) {
    String sql = "<script>\n"
        + "select \n"
        + "    <if test=\"distinct != null and distinct\">\n"
        + "      distinct\n"
        + "    </if>\n"
        + "<choose>\n"
        + " <when test=\"selectFields != null and selectFields.size != 0\">\n"
        + "    <foreach collection=\"selectFields\" index=\"index\" item=\"item\" separator=\",\">\n"
        + "      ${item}\n"
        + "    </foreach>\n"
        + " </when>\n"
        + " <when test=\"relateFields != null and relateFields.size != 0\">\n"
        + "    <foreach collection=\"relateFields\" index=\"index\" item=\"item\" separator=\",\">\n"
        + "      ${item}\n"
        + "    </foreach>\n"
        + " </when>\n"
        + " <otherwise>\n"
        + "%s \n"
        + " </otherwise>\n"
        + "</choose>\n"
        + "from %s t_s0 left join %s t_s1 on t_s0.%s = t_s1.%s\n"
        + "${conditionSql}</script>";
    String selectFields = sqlSelectColumns(tableInfo);
    sql = String.format(sql, selectFields, tableInfo.getParentTableName(), tableInfo.getTableName(),
        tableInfo.getParentKeyColumn(), tableInfo.getKeyColumn());
    SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Map.class);
    return this.addSelectMappedStatementForTable(mapperClass, "selectByDynamicCondition", sqlSource,
        tableInfo);
  }

  /**
   * SQL 查询所有表字段
   *
   * @param table 表信息
   * @return sql 脚本
   */
  private String sqlSelectColumns(ExtTableInfo table) {
    /* 假设存在 resultMap 映射返回 */
    String selectColumns = ASTERISK;
    if (table.getResultMap() == null || (table.getResultMap() != null && table.isInitResultMap())) {
      /* 普通查询 */
      selectColumns = table.getAllSqlSelect();
    }
    return selectColumns;
  }
}
