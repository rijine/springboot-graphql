package com.yonyou.einvoice.common.agile.mp.extend.methods;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import com.yonyou.einvoice.common.agile.mp.extend.AbstractExtMethod;
import com.yonyou.einvoice.common.agile.mp.extend.ExtSqlMethod;
import com.yonyou.einvoice.common.agile.mp.extend.ExtTableFieldInfo;
import com.yonyou.einvoice.common.agile.mp.extend.ExtTableInfo;
import java.util.List;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 扩展表的批量插入方法。 -- 基于扩展表，且只用于扩展表的插入，主表不会进行插入
 *
 * @author liuqiangm
 * @since 2019-11-13
 */
@NoArgsConstructor
@AllArgsConstructor
public class ExtInsertBatchSomeColumn extends AbstractExtMethod {

  /**
   * mapper 对应的方法名
   */
  private static final String MAPPER_METHOD = "insertBatchSomeColumn";

  /**
   * 字段筛选条件
   */
  @Setter
  @Accessors(chain = true)
  private Predicate<ExtTableFieldInfo> predicate;

  @SuppressWarnings("Duplicates")
  @Override
  public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass,
      ExtTableInfo tableInfo) {
    KeyGenerator keyGenerator = new NoKeyGenerator();
    ExtSqlMethod sqlMethod = ExtSqlMethod.INSERT_ONE;
    List<TableFieldInfo> selfFieldList = tableInfo.getSelfFieldList();
    String insertSqlColumn = tableInfo.getKeyInsertSqlColumn(false) +
        this.filterTableFieldInfo(selfFieldList, predicate,
            ExtTableFieldInfo::getInsertSqlColumn, EMPTY);
    String columnScript =
        LEFT_BRACKET + insertSqlColumn.substring(0, insertSqlColumn.length() - 1) + RIGHT_BRACKET;
    String insertSqlProperty = tableInfo.getKeyInsertSqlProperty(ENTITY_DOT, false) +
        this.filterTableFieldInfo(selfFieldList, predicate, i -> i.getInsertSqlProperty(ENTITY_DOT),
            EMPTY);
    insertSqlProperty =
        LEFT_BRACKET + insertSqlProperty.substring(0, insertSqlProperty.length() - 1)
            + RIGHT_BRACKET;
    String valuesScript = SqlScriptUtils
        .convertForeach(insertSqlProperty, "list", null, ENTITY, COMMA);
    String keyProperty = tableInfo.getKeyProperty();
    String keyColumn = tableInfo.getKeyColumn();
    String sql = String
        .format(sqlMethod.getSql(), tableInfo.getTableName(), columnScript, valuesScript);
    SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
    return this
        .addInsertMappedStatement(mapperClass, modelClass, MAPPER_METHOD, sqlSource, keyGenerator,
            keyProperty, keyColumn);
  }
}
