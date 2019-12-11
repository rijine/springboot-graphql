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
 * 根据 ID 更新有值字段 TODO 在扩展表不存在数据时新增
 *
 * @author hubin
 * @since 2018-04-06
 */
public class ExtUpdateById extends AbstractExtMethod {

  @Override
  public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass,
      ExtTableInfo tableInfo) {
    boolean logicDelete = tableInfo.isLogicDelete();
    ExtSqlMethod sqlMethod = ExtSqlMethod.UPDATE_BY_ID;
    final String additional = optlockVersion() + tableInfo.getLogicDeleteSql(true, false);
    // 由于是left join，因此id需要改成parentKeyColumn
    String sql = String.format(sqlMethod.getSql(),
        tableInfo.getParentTableName(), tableInfo.getTableName(), tableInfo.getParentKeyColumn(),
        tableInfo.getKeyColumn(),
        sqlSet(logicDelete, false, tableInfo, false, ENTITY, ENTITY_DOT),
        tableInfo.getParentKeyColumn(), ENTITY_DOT + tableInfo.getKeyProperty(), additional);
    SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
    return addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
  }
}
