package com.yonyou.einvoice.common.agile.mp.methods;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import java.util.Map;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 注入selectDynamic
 *
 * @author liuqiangm
 */
public class SelectDynamicMethod extends AbstractMethod {

  @Override
  public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass,
      TableInfo tableInfo) {
    String sql = "<script>\n"
        + "select \n"
        + "<if test=\"distinct != null and distinct\">\n"
        + "distinct\n"
        + "</if>\n"
        + "<choose>\n"
        + " <when test=\"selectFields != null and selectFields.size != 0\">\n"
        + "    <foreach collection=\"selectFields\" index=\"index\" item=\"item\" separator=\",\">\n"
        + "      t0.${item}\n"
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
        + "from %s t0\n"
        + "${conditionSql}\n"
        + "</script>";
    String selectFields = sqlSelectColumns(tableInfo, false);
    sql = String.format(sql, selectFields, tableInfo.getTableName());
    SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Map.class);
    return this.addSelectMappedStatementForTable(mapperClass, "selectByDynamicCondition", sqlSource,
        tableInfo);
  }

}
