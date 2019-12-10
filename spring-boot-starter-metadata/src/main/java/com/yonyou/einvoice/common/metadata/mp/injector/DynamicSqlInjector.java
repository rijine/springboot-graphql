package com.yonyou.einvoice.common.metadata.mp.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.injector.methods.additional.InsertBatchSomeColumn;
import com.yonyou.einvoice.common.metadata.mp.anno.ExtensionMeta;
import com.yonyou.einvoice.common.metadata.mp.anno.InsertBatchIgnore;
import com.yonyou.einvoice.common.metadata.mp.extend.AbstractExtMethod;
import com.yonyou.einvoice.common.metadata.mp.extend.ExtTableInfo;
import com.yonyou.einvoice.common.metadata.mp.extend.ExtTableInfoHelper;
import com.yonyou.einvoice.common.metadata.mp.extend.methods.ExtCountAllDynamicMethod;
import com.yonyou.einvoice.common.metadata.mp.extend.methods.ExtDelete;
import com.yonyou.einvoice.common.metadata.mp.extend.methods.ExtDeleteBatchByIds;
import com.yonyou.einvoice.common.metadata.mp.extend.methods.ExtDeleteById;
import com.yonyou.einvoice.common.metadata.mp.extend.methods.ExtDeleteByMap;
import com.yonyou.einvoice.common.metadata.mp.extend.methods.ExtInsert;
import com.yonyou.einvoice.common.metadata.mp.extend.methods.ExtInsertBatchSomeColumn;
import com.yonyou.einvoice.common.metadata.mp.extend.methods.ExtSelectBatchByIds;
import com.yonyou.einvoice.common.metadata.mp.extend.methods.ExtSelectById;
import com.yonyou.einvoice.common.metadata.mp.extend.methods.ExtSelectByMap;
import com.yonyou.einvoice.common.metadata.mp.extend.methods.ExtSelectCount;
import com.yonyou.einvoice.common.metadata.mp.extend.methods.ExtSelectDynamicMethod;
import com.yonyou.einvoice.common.metadata.mp.extend.methods.ExtSelectList;
import com.yonyou.einvoice.common.metadata.mp.extend.methods.ExtSelectMaps;
import com.yonyou.einvoice.common.metadata.mp.extend.methods.ExtSelectMapsPage;
import com.yonyou.einvoice.common.metadata.mp.extend.methods.ExtSelectObjs;
import com.yonyou.einvoice.common.metadata.mp.extend.methods.ExtSelectOne;
import com.yonyou.einvoice.common.metadata.mp.extend.methods.ExtSelectPage;
import com.yonyou.einvoice.common.metadata.mp.extend.methods.ExtUpdate;
import com.yonyou.einvoice.common.metadata.mp.extend.methods.ExtUpdateById;
import com.yonyou.einvoice.common.metadata.mp.methods.CountAllDynamicMethod;
import com.yonyou.einvoice.common.metadata.mp.methods.SelectDynamicMethod;
import com.yonyou.einvoice.common.metadata.mp.repository.IExtendMetaMapper;
import com.yonyou.einvoice.common.metadata.mp.repository.IMetaMapper;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.Configuration;
import org.springframework.stereotype.Component;

/**
 * sql注入器。用于增强mapper
 *
 * @author liuqiangm
 */
@Setter
@Getter
@Component
@Slf4j
public class DynamicSqlInjector extends DefaultSqlInjector {

  public static final String SELECT_FIELDS = "selectFields";

  public static final String CONDITION_SQL = "conditionSql";

  public static final String DISTINCT = "distinct";

  /**
   * 表column到属性property的映射。key为表名，value为column->property映射
   */
  public static final Map<String, Map<String, String>> COLUMN_2_PROPERTY_MAP_OF_ENTITY = new HashMap<>();
  /**
   * 属性property到表column的映射。key为表名，value为property->column映射
   */
  public static final Map<String, Map<String, String>> PROPERTY_2_COLUMN_MAP_OF_ENTITY = new HashMap<>();

  /**
   * 实体名 -> 主键列名映射
   */
  public static final Map<String, String> KEY_COLUMN_MAP = new HashMap<>();

  /**
   * 实体名 -> 主键实体属性名映射
   */
  public static final Map<String, String> KEY_PROPERTY_MAP = new HashMap<>();

  public static final Map<String, String> BEAN_TO_TABLE_MAP = new HashMap();

  public static final Map<String, ExtTableInfo> EXTEND_TABLE_INFO_MAP = new HashMap<>();

  public static final Map<String, List<Object>> RESULTMAP_MAP = new HashMap<>();

  public static Configuration configuration = null;

  private void addResultMap(String entityName, TableInfo tableInfo,
      MapperBuilderAssistant builderAssistant) {
    if (configuration == null) {
      configuration = builderAssistant.getConfiguration();
    }
    String resultMapId = tableInfo.getResultMap();
    if (resultMapId != null) {
      List<Object> list = new ArrayList<>(2);
      list.add(resultMapId);
      ResultMap resultMap = builderAssistant.getConfiguration().getResultMap(resultMapId);
      list.add(resultMap);
      RESULTMAP_MAP.put(entityName, list);
    }
  }

  @Override
  public void inspectInject(MapperBuilderAssistant builderAssistant, Class<?> mapperClass) {
    Class<?> modelClass = extractModelClass(mapperClass);
    if (modelClass == null) {
      return;
    }
    String className = modelClass.getName();
    Set<String> mapperRegistryCache = GlobalConfigUtils
        .getMapperRegistryCache(builderAssistant.getConfiguration());
    /**
     * 如果mapper继承了DynamicConditionMapper
     */
    if (!mapperRegistryCache.contains(className)
        && IMetaMapper.class.isAssignableFrom(mapperClass) && !IExtendMetaMapper.class
        .isAssignableFrom(mapperClass)) {
      Set<String> ignoreSet = getInsertBatchIgnorePropertySet(modelClass);
      List<AbstractMethod> methodList = this.getDynamicMethodList(mapperClass, ignoreSet);
      if (CollectionUtils.isNotEmpty(methodList)) {
        TableInfo tableInfo = TableInfoHelper.initTableInfo(builderAssistant, modelClass);
        addResultMap(className, tableInfo, builderAssistant);
        // 将当前表与实体的映射关系进行缓存
        cacheTableInfo(tableInfo);
        // 循环注入自定义方法
        methodList.forEach(m -> m.inject(builderAssistant, mapperClass, modelClass, tableInfo));
        methodList = super.getMethodList(modelClass);
        methodList.forEach(m -> m.inject(builderAssistant, mapperClass, modelClass, tableInfo));
      } else {
        log.info(mapperClass.toString() + ", No effective injection method was found.");
      }
      mapperRegistryCache.add(mapperClass.toString());
      return;
    }
    if (!mapperRegistryCache.contains(className) && IExtendMetaMapper.class
        .isAssignableFrom(mapperClass)) {
      Set<String> ignoreSet = getInsertBatchIgnorePropertySet(modelClass);
      List<AbstractExtMethod> methodList = this.getExtendMethodList(mapperClass, ignoreSet);
      ExtensionMeta extensionMeta = mapperClass.getAnnotation(ExtensionMeta.class);
      Class superEntityClazz = extensionMeta.entityClazz();
      if (CollectionUtils.isNotEmpty(methodList)) {
        // 扩展类的TableInfo
        ExtTableInfo extendTableInfo = ExtTableInfoHelper
            .initTableInfo(builderAssistant, modelClass);
        addResultMap(className, extendTableInfo, builderAssistant);
        EXTEND_TABLE_INFO_MAP.put(superEntityClazz.getName(), extendTableInfo);
        // 将当前表与实体的映射关系进行缓存
        cacheTableInfo(extendTableInfo);
        // 循环注入自定义方法。 TODO 方法实现尚需更改
        methodList
            .forEach(m -> m.inject(builderAssistant, mapperClass, modelClass, extendTableInfo));
      } else {
        log.info(mapperClass.toString() + ", No effective injection method was found.");
      }
      mapperRegistryCache.add(mapperClass.toString());
      return;
    }
    // 兼容原mybatis-plus的使用场景
    super.inspectInject(builderAssistant, mapperClass);

  }

  /**
   * 在原有insert方法的基础上，注入selectDynamic和countAllDynamic方法。
   *
   * @param mapperClass
   * @return
   */
  public List<AbstractMethod> getDynamicMethodList(Class<?> mapperClass, Set<String> ignoreSet) {
    List<AbstractMethod> methods = new LinkedList<>();
    methods.addAll(getMethodList(mapperClass));
    methods.add(new SelectDynamicMethod());
    methods.add(new CountAllDynamicMethod());
    InsertBatchSomeColumn batchSomeColumn = new InsertBatchSomeColumn();
    batchSomeColumn.setPredicate((column) -> {
      boolean result = !ignoreSet.contains(column.getProperty());
      return result;
    });
    methods.add(batchSomeColumn);
    return methods;
  }

  /**
   * 针对扩展类定制的ExtendAbstractMethod
   *
   * @param mapperClass
   * @return
   */
  public List<AbstractExtMethod> getExtendMethodList(Class<?> mapperClass,
      Set<String> ignoreSet) {
    List<AbstractExtMethod> methods = new LinkedList<>();
    methods.add(new ExtCountAllDynamicMethod());
    methods.add(new ExtDelete());
    methods.add(new ExtDeleteBatchByIds());
    methods.add(new ExtDeleteById());
    methods.add(new ExtDeleteByMap());
    methods.add(new ExtInsert());
    methods.add(new ExtInsertBatchSomeColumn()
        .setPredicate((column) -> {
          boolean result = !ignoreSet.contains(column.getProperty());
          return result;
        }));
    methods.add(new ExtSelectBatchByIds());
    methods.add(new ExtSelectById());
    methods.add(new ExtSelectByMap());
    methods.add(new ExtSelectCount());
    methods.add(new ExtSelectDynamicMethod());
    methods.add(new ExtSelectList());
    methods.add(new ExtSelectMaps());
    methods.add(new ExtSelectMapsPage());
    methods.add(new ExtSelectObjs());
    methods.add(new ExtSelectOne());
    methods.add(new ExtSelectPage());
    methods.add(new ExtUpdate());
    methods.add(new ExtUpdateById());
    return methods;
  }

  /**
   * 将表字段信息缓存到Map当中
   *
   * @param tableInfo
   */
  private void cacheTableInfo(TableInfo tableInfo) {
    String entityName = tableInfo.getEntityType().getName();
    String keyColumn = tableInfo.getKeyColumn();
    String keyProperty = tableInfo.getKeyProperty();
    List<TableFieldInfo> tableFieldInfoList = tableInfo.getFieldList();
    if (CollectionUtils.isEmpty(tableFieldInfoList)) {
      return;
    }
    Map<String, String> column2PropertyMap = new HashMap<>(
        (int) ((tableFieldInfoList.size() + 1) / 0.75) + 1);
    Map<String, String> property2ColumnMap = new HashMap<>(
        (int) ((tableFieldInfoList.size() + 1) / 0.75) + 1);
    column2PropertyMap.put(keyColumn, keyProperty);
    property2ColumnMap.put(keyProperty, keyColumn);
    for (TableFieldInfo tableFieldInfo : tableFieldInfoList) {
      column2PropertyMap.put(tableFieldInfo.getColumn(), tableFieldInfo.getProperty());
      property2ColumnMap.put(tableFieldInfo.getProperty(), tableFieldInfo.getColumn());
    }
    // 将当前表信息存入从table相关的映射当中。如果扩展表先于主表初始化，则无需处理
    if (!COLUMN_2_PROPERTY_MAP_OF_ENTITY.containsKey(entityName)) {
      COLUMN_2_PROPERTY_MAP_OF_ENTITY.put(entityName, column2PropertyMap);
      PROPERTY_2_COLUMN_MAP_OF_ENTITY.put(entityName, property2ColumnMap);
      KEY_COLUMN_MAP.put(entityName, tableInfo.getKeyColumn());
      KEY_PROPERTY_MAP.put(entityName, tableInfo.getKeyProperty());
      BEAN_TO_TABLE_MAP.put(entityName, tableInfo.getTableName());
    }
    // 如果当前为扩展，则将扩展字段修正到源主表字段映射当中。
    if (tableInfo instanceof ExtTableInfo) {
      entityName = tableInfo.getEntityType().getSuperclass().getName();
      COLUMN_2_PROPERTY_MAP_OF_ENTITY.put(entityName, column2PropertyMap);
      PROPERTY_2_COLUMN_MAP_OF_ENTITY.put(entityName, property2ColumnMap);
      KEY_COLUMN_MAP.put(entityName, tableInfo.getKeyColumn());
      KEY_PROPERTY_MAP.put(entityName, tableInfo.getKeyProperty());
      BEAN_TO_TABLE_MAP.put(entityName, tableInfo.getTableName());
    }
  }

  private Set<String> getInsertBatchIgnorePropertySet(Class modelClass) {
    Set<String> ignoreSet = new HashSet<>();

    java.lang.reflect.Field[] fields = modelClass.getDeclaredFields();
    for (java.lang.reflect.Field field : fields) {
      Annotation insertAnnotation = field.getAnnotation(InsertBatchIgnore.class);
      if (insertAnnotation != null) {
        ignoreSet.add(field.getName());
      }
    }
    return ignoreSet;
  }
}
