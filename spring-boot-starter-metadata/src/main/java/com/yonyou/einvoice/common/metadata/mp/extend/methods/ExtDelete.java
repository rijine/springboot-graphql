/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.yonyou.einvoice.common.metadata.mp.extend.methods;

import com.yonyou.einvoice.common.metadata.mp.extend.AbstractExtMethod;
import com.yonyou.einvoice.common.metadata.mp.extend.ExtSqlMethod;
import com.yonyou.einvoice.common.metadata.mp.extend.ExtTableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 扩展类的删除
 *
 * @author liuqiangm
 * @since 201
 */
public class ExtDelete extends AbstractExtMethod {

  @Override
  public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass,
      ExtTableInfo tableInfo) {
    String sql;
    ExtSqlMethod sqlMethod = ExtSqlMethod.LOGIC_DELETE;
    if (tableInfo.isLogicDelete()) {
      sql = String.format(sqlMethod.getSql(),
          tableInfo.getParentTableName(), tableInfo.getTableName(), tableInfo.getParentKeyColumn(),
          tableInfo.getKeyColumn(),
          sqlLogicSet(tableInfo),
          sqlExtWhereEntityWrapper(true, tableInfo),
          sqlComment());
      SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
      return addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
    } else {

      sqlMethod = ExtSqlMethod.DELETE;
      sql = String.format(sqlMethod.getSql(),
          tableInfo.getParentTableName(), tableInfo.getTableName(),
          tableInfo.getParentTableName(), tableInfo.getTableName(), tableInfo.getParentKeyColumn(),
          tableInfo.getKeyColumn(),
          sqlExtWhereEntityWrapper(true, tableInfo),
          sqlComment());
      SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
      return this.addDeleteMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource);
    }
  }
}
