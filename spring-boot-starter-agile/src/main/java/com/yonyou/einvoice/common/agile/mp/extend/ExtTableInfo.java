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
package com.yonyou.einvoice.common.agile.mp.extend;

import static java.util.stream.Collectors.joining;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;

/**
 * 数据库表反射信息（针对扩展表）
 *
 * @author liuqiangm
 * @since 2019-11-12
 */
@Setter
@Getter
@ToString
@Accessors(chain = true)
public class ExtTableInfo extends TableInfo {

  /**
   * 实体类型
   */
  private Class<?> entityType;
  /**
   * 表主键ID 类型
   */
  private IdType idType = IdType.NONE;
  /**
   * 表名称
   */
  private String tableName;
  /**
   * 源表名称
   */
  private String parentTableName;
  /**
   * 表映射结果集
   */
  private String resultMap;
  /**
   * 是否是需要自动生成的 resultMap
   */
  private boolean autoInitResultMap;
  /**
   * 是否是自动生成的 resultMap
   */
  private boolean initResultMap;
  /**
   * 主键是否有存在字段名与属性名关联
   * <p>true: 表示要进行 as</p>
   */
  private boolean keyRelated = false;
  /**
   * 表主键ID 字段名
   */
  private String keyColumn;
  /**
   * 表主键ID 属性名
   */
  private String keyProperty;
  /**
   * 表主键ID 属性反射的field
   */
  private Field keyField;
  /**
   * 表主键ID 属性类型
   */
  private Class<?> keyType;
  /**
   * 表主键ID Sequence
   */
  private KeySequence keySequence;
  /**
   * 源表主键ID 字段名
   */
  private String parentKeyColumn;
  /**
   * 源表主键ID 属性名
   */
  private String parentKeyProperty;
  /**
   * 源实体主键类型
   */
  private Class<?> parentKeyType;
  /**
   * 表主键ID 属性反射的field
   */
  private Field parentKeyField;
  /**
   * 表字段信息列表
   */
  private List<TableFieldInfo> fieldList;
  /**
   * class自身的表字段信息列表
   */
  private List<TableFieldInfo> selfFieldList;
  /**
   * class的父类的表字段信息列表
   */
  private List<TableFieldInfo> parentFieldList;
  /**
   * 命名空间 (对应的 mapper 接口的全类名)
   */
  private String currentNamespace;
  /**
   * MybatisConfiguration 标记 (Configuration内存地址值)
   */
  @Getter
  private MybatisConfiguration configuration;
  /**
   * 是否开启逻辑删除
   */
  private boolean logicDelete = false;
  /**
   * 是否开启下划线转驼峰
   */
  private boolean underCamel = true;
  /**
   * 缓存包含主键及字段的 sql select
   */
  @Setter(AccessLevel.NONE)
  @Getter(AccessLevel.NONE)
  private String allSqlSelect;
  /**
   * 缓存主键字段的 sql select
   */
  @Setter(AccessLevel.NONE)
  @Getter(AccessLevel.NONE)
  private String sqlSelect;

  public ExtTableInfo(Class<?> entityType) {
    super(entityType);
    this.entityType = entityType;
  }

  /**
   * 获得注入的 SQL Statement
   *
   * @param sqlMethod MybatisPlus 支持 SQL 方法
   * @return SQL Statement
   */
  @Override
  public String getSqlStatement(String sqlMethod) {
    return currentNamespace + DOT + sqlMethod;
  }


  /**
   * 设置 Configuration
   */
  void setConfiguration(Configuration configuration) {
    Assert.notNull(configuration, "Error: You need Initialize MybatisConfiguration !");
    this.configuration = (MybatisConfiguration) configuration;
    this.underCamel = configuration.isMapUnderscoreToCamelCase();
  }

  /**
   * 设置逻辑删除
   */
  void setLogicDelete(boolean logicDelete) {
    if (logicDelete) {
      this.logicDelete = true;
    }
  }

  /**
   * 获取主键的 select sql 片段
   *
   * @return sql 片段
   */
  @Override
  public String getKeySqlSelect() {
    if (sqlSelect != null) {
      return sqlSelect;
    }
    if (StringUtils.isNotEmpty(keyProperty)) {
      sqlSelect = keyColumn;
      if (keyRelated) {
        sqlSelect += (" AS " + keyProperty);
      }
    } else {
      sqlSelect = EMPTY;
    }
    return sqlSelect;
  }

  /**
   * 获取包含主键及字段的 select sql 片段
   *
   * @return sql 片段
   */
  @Override
  public String getAllSqlSelect() {
    if (allSqlSelect != null) {
      return allSqlSelect;
    }
    allSqlSelect = chooseSelect(TableFieldInfo::isSelect);
    return allSqlSelect;
  }

  /**
   * 获取需要进行查询的 select sql 片段
   *
   * @param predicate 过滤条件
   * @return sql 片段
   */
  @Override
  public String chooseSelect(Predicate<TableFieldInfo> predicate) {
    String sqlSelect = getKeySqlSelect();
    String fieldsSqlSelect = fieldList.stream().filter(predicate)
        .map(TableFieldInfo::getSqlSelect).collect(joining(COMMA));
    if (StringUtils.isNotEmpty(sqlSelect) && StringUtils.isNotEmpty(fieldsSqlSelect)) {
      return sqlSelect + COMMA + fieldsSqlSelect;
    } else if (StringUtils.isNotEmpty(fieldsSqlSelect)) {
      return fieldsSqlSelect;
    }
    return sqlSelect;
  }

  /**
   * 获取 insert 时候主键 sql 脚本片段
   * <p>insert into table (字段) values (值)</p>
   * <p>位于 "值" 部位</p>
   *
   * @return sql 脚本片段
   */
  @Override
  public String getKeyInsertSqlProperty(final String prefix, final boolean newLine) {
    final String newPrefix = prefix == null ? EMPTY : prefix;
    if (StringUtils.isNotEmpty(keyProperty)) {
      return SqlScriptUtils.safeParam(newPrefix + keyProperty) + COMMA + (newLine ? NEWLINE
          : EMPTY);
    }
    return EMPTY;
  }

  /**
   * 获取 insert 时候主键 sql 脚本片段
   * <p>insert into table (字段) values (值)</p>
   * <p>位于 "字段" 部位</p>
   *
   * @return sql 脚本片段
   */
  @Override
  public String getKeyInsertSqlColumn(final boolean newLine) {
    if (StringUtils.isNotEmpty(keyColumn)) {
      return keyColumn + COMMA + (newLine ? NEWLINE : EMPTY);
    }
    return EMPTY;
  }

  /**
   * 获取所有 insert 时候插入值 sql 脚本片段
   * <p>insert into table (字段) values (值)</p>
   * <p>位于 "值" 部位</p>
   *
   * <li> 自动选部位,根据规则会生成 if 标签 </li>
   *
   * @return sql 脚本片段
   */
  @Override
  public String getAllInsertSqlPropertyMaybeIf(final String prefix) {
    final String newPrefix = prefix == null ? EMPTY : prefix;
    return getKeyInsertSqlProperty(newPrefix, true) + selfFieldList.stream()
        .map(i -> i.getInsertSqlPropertyMaybeIf(newPrefix)).filter(Objects::nonNull)
        .collect(joining(NEWLINE));
  }

  /**
   * 对扩展表进行插入操作时，需要仅对扩展表字段生成插入语句。
   *
   * @return sql 脚本片段
   */
  @Override
  public String getAllInsertSqlColumnMaybeIf() {
    return getKeyInsertSqlColumn(true) + selfFieldList.stream()
        .map(TableFieldInfo::getInsertSqlColumnMaybeIf)
        .filter(Objects::nonNull).collect(joining(NEWLINE));
  }

  /**
   * 获取所有的查询的 sql 片段
   *
   * @param ignoreLogicDelFiled 是否过滤掉逻辑删除字段
   * @param withId              是否包含 id 项
   * @param prefix              前缀
   * @return sql 脚本片段
   */
  @Override
  public String getAllSqlWhere(boolean ignoreLogicDelFiled, boolean withId, final String prefix) {
    final String newPrefix = prefix == null ? EMPTY : prefix;
    return sqlWhere(ignoreLogicDelFiled, withId, newPrefix, fieldList);
  }

  /**
   * 获取所有的查询的 sql 片段
   *
   * @param ignoreLogicDelFiled 是否过滤掉逻辑删除字段
   * @param withId              是否包含 id 项
   * @param prefix              前缀
   * @return sql 脚本片段
   */
  public String getSelfSqlWhere(boolean ignoreLogicDelFiled, boolean withId, final String prefix) {
    final String newPrefix = prefix == null ? EMPTY : prefix;
    return sqlWhere(ignoreLogicDelFiled, withId, newPrefix, selfFieldList);
  }

  private String sqlWhere(boolean ignoreLogicDelFiled, boolean withId, final String newPrefix,
      List<TableFieldInfo> infoList) {
    String filedSqlScript = infoList.stream()
        .filter(i -> {
          if (ignoreLogicDelFiled) {
            return !(isLogicDelete() && ((ExtTableFieldInfo) i).isLogicDelete());
          }
          return true;
        })
        .map(i -> i.getSqlWhere(newPrefix)).filter(Objects::nonNull).collect(joining(NEWLINE));
    if (!withId || StringUtils.isEmpty(keyProperty)) {
      return filedSqlScript;
    }
    String newKeyProperty = newPrefix + keyProperty;
    String keySqlScript = keyColumn + EQUALS + SqlScriptUtils.safeParam(newKeyProperty);
    return
        SqlScriptUtils.convertIf(keySqlScript, String.format("%s != null", newKeyProperty), false)
            + NEWLINE + filedSqlScript;
  }

  /**
   * 获取所有的 sql set 片段
   *
   * @param ignoreLogicDelFiled 是否过滤掉逻辑删除字段
   * @param prefix              前缀
   * @return sql 脚本片段
   */
  @Override
  public String getAllSqlSet(boolean ignoreLogicDelFiled, final String prefix) {
    final String newPrefix = prefix == null ? EMPTY : prefix;
    return fieldList.stream()
        .filter(i -> {
          if (ignoreLogicDelFiled) {
            return !(isLogicDelete() && i.isLogicDelete());
          }
          return true;
        }).map(i -> i.getSqlSet(newPrefix)).filter(Objects::nonNull).collect(joining(NEWLINE));
  }

  /**
   * 获取逻辑删除字段的 sql 脚本
   *
   * @param startWithAnd 是否以 and 开头
   * @param deleteValue  是否需要的是逻辑删除值
   * @return sql 脚本
   */
  @Override
  public String getLogicDeleteSql(boolean startWithAnd, boolean deleteValue) {
    if (logicDelete) {
      TableFieldInfo field = fieldList.stream().filter(TableFieldInfo::isLogicDelete).findFirst()
          .orElseThrow(
              () -> ExceptionUtils.mpe("can't find the logicFiled from table {%s}", tableName));
      String formatStr = field.isCharSequence() ? "'%s'" : "%s";
      String logicDeleteSql = field.getColumn() + EQUALS +
          String.format(formatStr,
              deleteValue ? field.getLogicDeleteValue() : field.getLogicNotDeleteValue());
      if (startWithAnd) {
        logicDeleteSql = " AND " + logicDeleteSql;
      }
      return logicDeleteSql;
    }
    return EMPTY;
  }

  /**
   * 自动构建 resultMap 并注入(如果条件符合的话)
   */
  public void initResultMapIfNeed() {
    if (resultMap == null && autoInitResultMap) {
      String id = currentNamespace + DOT + MYBATIS_PLUS + UNDERSCORE + entityType.getSimpleName();
      List<ResultMapping> resultMappings = new ArrayList<>();
      if (parentKeyType != null) {
        ResultMapping idMapping = new ResultMapping.Builder(configuration, parentKeyProperty,
            parentKeyColumn,
            parentKeyType)
            .flags(Collections.singletonList(ResultFlag.ID)).build();
        resultMappings.add(idMapping);
      }
      if (CollectionUtils.isNotEmpty(parentFieldList)) {
        parentFieldList.forEach(
            i -> resultMappings.add(((ExtTableFieldInfo) i).getResultMapping(configuration)));
      }
      if (CollectionUtils.isNotEmpty(selfFieldList)) {
        selfFieldList.forEach(
            i -> resultMappings.add(((ExtTableFieldInfo) i).getResultMapping(configuration)));
      }
      ResultMapping mapping = new ResultMapping.Builder(configuration, keyProperty, keyColumn,
          keyType).build();
      resultMappings.add(mapping);
      ResultMap resultMap = new ResultMap.Builder(configuration, id, entityType, resultMappings)
          .build();
      configuration.addResultMap(resultMap);
      this.resultMap = id;
    }
  }
}