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
import java.util.Map;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 查询满足条件所有数据 -- 基于扩展表
 *
 * @author liuqiangm
 * @since 2019-11-13
 */
public class ExtSelectMaps extends AbstractExtMethod {

  @Override
  public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass,
      ExtTableInfo tableInfo) {
    ExtSqlMethod sqlMethod = ExtSqlMethod.SELECT_MAPS;
    String sql = String.format(sqlMethod.getSql(), sqlSelectColumns(tableInfo, true),
        tableInfo.getParentTableName(), tableInfo.getTableName(), tableInfo.getParentKeyColumn(),
        tableInfo.getKeyColumn(),
        sqlWhereEntityWrapper(true, tableInfo),
        sqlComment());
    SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
    return this
        .addSelectMappedStatementForOther(mapperClass, sqlMethod.getMethod(), sqlSource, Map.class);
  }
}
