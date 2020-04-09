package com.yonyou.einvoice.common.agile.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.esotericsoftware.reflectasm.FieldAccess;
import com.google.common.collect.Lists;
import com.yonyou.einvoice.common.agile.element.EntityCondition;
import com.yonyou.einvoice.common.agile.mp.anno.ExtensionMeta;
import com.yonyou.einvoice.common.agile.mp.extend.ExtTableFieldInfo;
import com.yonyou.einvoice.common.agile.mp.extend.ExtTableInfo;
import com.yonyou.einvoice.common.agile.mp.injector.DynamicSqlInjector;
import com.yonyou.einvoice.common.agile.mp.relate.Many;
import com.yonyou.einvoice.common.agile.mp.relate.One;
import com.yonyou.einvoice.common.agile.mp.repository.IExtendMetaMapper;
import com.yonyou.einvoice.common.agile.mp.repository.IMetaMapper;
import com.yonyou.einvoice.common.agile.visitor.MybatisCountAllSqlVisitor;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.ResultMapping.Builder;
import org.apache.ibatis.session.Configuration;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Slf4j
public class AbstractMybatisService<T, Q extends IMetaMapper> extends
    AbstractService<T, Q> {

  private static final String ID = "id";
  private static final String ID_RESULT_MAPPINGS = "idResultMappings";
  private static final String MAPPED_COLUMNS = "mappedColumns";
  private static final String MAPPED_PROPERTIES = "mappedProperties";
  private static final String PROPERTY_RESULT_MAPPINGS = "propertyResultMappings";
  private static final String RESULT_MAPPINGS = "resultMappings";
  private static final String TYPE = "type";
  /**
   * 分批大小。例如，批量插入时每1000个插一次。 根据id查询实体时，每次查1000个id，然后拼接返回结果。
   */
  private final int BATCH_SIZE = 1000;


  /**
   * bean类型T所对应的数据库表名
   */
  private String tableName;
  /**
   * 被扩展的数据库表名
   */
  private String parentTableName;

  /**
   * 主键column
   */
  private String keyColumn;

  /**
   * 主键的java property
   */
  private String keyProperty;

  /**
   * 主键的field
   */
  private Field keyField;

  private FieldAccess keyFieldAccess;

  private int keyFieldIndex;

  /**
   * 父表的主键field
   */
  private Field parentKeyField;

  private FieldAccess parentKeyFieldAccess;

  private int parentKeyFieldIndex;

  //  @Autowired
  protected static final Set<ResultMap> resultMapSet = new HashSet<>();

  /**
   * 用于保存T数据类型的 数据库字段 -> java属性字段映射
   */
  private Map<String, String> column2PropertyMap;
  /**
   * 用于保存T数据类型的 java属性字段 -> 数据库字段映射
   */
  private Map<String, String> property2ColumnMap;

  protected String extEntityClassName;

  /**
   * 用于保存T类型中属性property -> java中的反射Field映射。 该映射用于通过反射，对java bean对象进行权限字段赋值操作。
   */
  protected Map<String, java.lang.reflect.Field> fieldMap = new ConcurrentHashMap<>();

  /**
   * GraphQL的类型验证器，用于在执行插入之前进行字段验证
   */
  protected Validator graphqlValidator;


  @Autowired
  protected ApplicationContext applicationContext;
  private volatile boolean firstInit = true;


  /**
   * 用于保存当前service所对应的实体类名称。（非扩展类）
   */
  protected String entityClassName;
  private List<String> relativeSelectFields = null;

  /**
   * 单例。惰性初始化，用于在不同的service之间公用
   */
  protected static volatile Map<String, IExtendMetaMapper> extendMetaMapperMap = null;

  /**
   * 初始化方法。用于初始化T数据类型的数据库字段 <--> java属性映射 不需要保证仅执行一次，可以执行多次，此处并没有进行严格的单例控制。
   * 原因在于：使用volatile可能会产生伪共享性能问题。
   */
  public void init() {
    if (!firstInit) {
      return;
    }
    // 获取泛型类型
    Class<T> rClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
        .getActualTypeArguments()[0];
    entityClassName = rClass.getName();
    column2PropertyMap = DynamicSqlInjector.COLUMN_2_PROPERTY_MAP_OF_ENTITY.get(rClass.getName());
    property2ColumnMap = DynamicSqlInjector.PROPERTY_2_COLUMN_MAP_OF_ENTITY.get(rClass.getName());
    keyColumn = DynamicSqlInjector.KEY_COLUMN_MAP.get(rClass.getName());
    keyProperty = DynamicSqlInjector.KEY_PROPERTY_MAP.get(rClass.getName());
    tableName = DynamicSqlInjector.BEAN_TO_TABLE_MAP.get(rClass.getName());
    // 惰性初始化extendMetaMapperMap
    singleton();
    // 如果当前实体存在扩展类，则获取扩展类的extendMapper
    extendMapper = extendMetaMapperMap.get(rClass.getName());
    // 当前实体类并未被扩展，则直接返回
    if (!DynamicSqlInjector.EXTEND_TABLE_INFO_MAP.containsKey(entityClassName)) {
      reviseResultMap(entityClassName, rClass, DynamicSqlInjector.configuration);
      return;
    }
    ExtTableInfo extendTableInfo = DynamicSqlInjector.EXTEND_TABLE_INFO_MAP.get(entityClassName);
    for (TableFieldInfo tableFieldInfo : extendTableInfo.getFieldList()) {
      ExtTableFieldInfo extendTableFieldInfo = (ExtTableFieldInfo) tableFieldInfo;
      fieldMap.put(extendTableFieldInfo.getProperty(), extendTableFieldInfo.getReflectField());
    }
    // 设置父表的字段集合
    parentTableColumnSet = extendTableInfo.getParentFieldList().stream()
        .map(TableFieldInfo::getColumn).collect(
            Collectors.toSet());
    // 设置子表的字段集合
    selfTableColumnSet = extendTableInfo.getSelfFieldList().stream().map(TableFieldInfo::getColumn)
        .collect(
            Collectors.toSet());
    parentTableColumnSet.add(extendTableInfo.getParentKeyColumn());
    selfTableColumnSet.add(extendTableInfo.getKeyColumn());
    parentKeyField = extendTableInfo.getParentKeyField();
    Class parentClass = parentKeyField.getDeclaringClass();
    parentKeyFieldAccess = FieldAccess.get(parentClass);
    parentKeyFieldIndex = parentKeyFieldAccess.getIndex(parentKeyField);
    parentTableName = extendTableInfo.getParentTableName();
    keyField = extendTableInfo.getKeyField();
    Class keyClass = keyField.getDeclaringClass();
    extEntityClassName = keyClass.getName();
    keyFieldAccess = FieldAccess.get(keyClass);
    keyFieldIndex = keyFieldAccess.getIndex(keyField);
    if (parentKeyField == null || keyField == null) {
      throw new RuntimeException("扩展类和被扩展类都必须包含主键字段。被扩展类：" + entityClassName);
    }
    reviseResultMap(keyClass.getName(), keyClass, DynamicSqlInjector.configuration);
    firstInit = false;
  }

  private void reviseResultMap(String entityClassName, Class entityClass,
      Configuration configuration) {
    if (configuration == null) {
      return;
    }
    List<Object> list = DynamicSqlInjector.RESULTMAP_MAP.get(entityClassName);
    List<Field> fieldList = ReflectionKit.getFieldList(ClassUtils.getUserClass(entityClass));
    List<Field> oneFieldList = new ArrayList<>();
    List<Field> manyFieldList = new ArrayList<>();
    for (Field field : fieldList) {
      One one = field.getAnnotation(One.class);
      Many many = field.getAnnotation(Many.class);
      if (one != null) {
        oneFieldList.add(field);
      }
      if (many != null) {
        manyFieldList.add(field);
      }
    }
    if (CollectionUtils.isEmpty(oneFieldList) && CollectionUtils.isEmpty(manyFieldList)) {
      return;
    }
    if (CollectionUtils.isEmpty(list)) {
      throw new RuntimeException(String
          .format("类%s中有字段使用了One或Many注解，则类上的TableName标签中，autoResultMapping属性必须设置为true",
              entityClassName));
    }
    List<ResultMapping> resultMappingList = new ArrayList<>();
    List<ResultMapping> propertyResultMappings = new ArrayList<>();
    ResultMap resultMap = (ResultMap) list.get(1);
    if (resultMapSet.contains(resultMap)) {
      return;
    }
    Set<String> mappedProperties = new HashSet<>(resultMap.getMappedProperties());
    propertyResultMappings.addAll(resultMap.getPropertyResultMappings());
    resultMappingList.addAll(resultMap.getResultMappings());
    if (!CollectionUtils.isEmpty(oneFieldList)) {
      for (Field field : oneFieldList) {
        Class subFieldClass = field.getType();
        String subFieldClassName = subFieldClass.getName();
        if (!DynamicSqlInjector.RESULTMAP_MAP.containsKey(subFieldClassName)) {
          throw new RuntimeException(String
              .format("类%s中的字段%s使用了One注解，但该字段的类型%s的TableName标签需要设置autoResultMapping属性为true",
                  entityClassName, field.getName(), subFieldClassName));
        }
        addFieldResultMapping(subFieldClassName, field, resultMappingList, propertyResultMappings,
            mappedProperties, configuration, "assoc_");
      }
    }
    if (!CollectionUtils.isEmpty(manyFieldList) && manyFieldList.size() != 1) {
      throw new RuntimeException(
          String.format("类%s中有多个字段使用了Many注解，Many注解在一个类中只能存在一次，请检查！", entityClassName));
    }
    if (!CollectionUtils.isEmpty(manyFieldList)) {
      for (Field field : manyFieldList) {
        Class subFieldClass = field.getType();
        if (Objects.equals(subFieldClass, List.class) || Collection.class
            .isAssignableFrom(subFieldClass)) {
          Type type = field.getGenericType();
          if (type instanceof ParameterizedType) {
            subFieldClass = (Class) (((ParameterizedType) type).getActualTypeArguments()[0]);
          }
        }
        String subFieldClassName = subFieldClass.getName();

        if (!DynamicSqlInjector.RESULTMAP_MAP.containsKey(subFieldClassName)) {
          throw new RuntimeException(String
              .format("类%s中的字段%s使用了Many注解，但该字段的类型%s的TableName标签需要设置autoResultMapping属性为true",
                  entityClassName, field.getName(), subFieldClassName));
        }
        addFieldResultMapping(subFieldClassName, field, resultMappingList, propertyResultMappings,
            mappedProperties, configuration, "collec_");
      }
    }
    try {
      Field propertyResultMappingsField = ResultMap.class
          .getDeclaredField("propertyResultMappings");
      Field resultMappingsField = ResultMap.class.getDeclaredField("resultMappings");
      Field mappedPropertiesField = ResultMap.class.getDeclaredField("mappedProperties");
      Field hasNestedResultMapsField = ResultMap.class.getDeclaredField("hasNestedResultMaps");
      propertyResultMappingsField.setAccessible(true);
      resultMappingsField.setAccessible(true);
      mappedPropertiesField.setAccessible(true);
      hasNestedResultMapsField.setAccessible(true);
      propertyResultMappingsField
          .set(resultMap, Collections.unmodifiableList(propertyResultMappings));
      resultMappingsField.set(resultMap, Collections.unmodifiableList(resultMappingList));
      hasNestedResultMapsField.set(resultMap, true);
      mappedPropertiesField.set(resultMap, Collections.unmodifiableSet(mappedProperties));
    } catch (Exception e) {
      log.error("", e);
    }

    log.info("entity {} revised ResultMap: {} ", entityClassName, JSON.toJSONString(resultMap));
    resultMapSet.add(resultMap);
  }

  private void addFieldResultMapping(String entityClassName, Field field,
      List<ResultMapping> resultMappingList, List<ResultMapping> propertyResultMappings,
      Set<String> mappedProperties, Configuration configuration, String prefix) {
    List<Object> list = DynamicSqlInjector.RESULTMAP_MAP.get(entityClassName);
    ResultMapping resultMapping = new Builder(configuration, field.getName())
        .javaType(field.getType())
        .nestedResultMapId(list.get(0).toString())
        .columnPrefix(prefix)
        .build();
    resultMappingList.add(resultMapping);
    propertyResultMappings.add(resultMapping);
    mappedProperties.add(field.getName());
  }

  private Map<String, Object> getFieldValueMap(ResultMap resultMap) {
    Map<String, Object> map = new HashMap<>();
    map.put(ID, resultMap.getId());
    map.put(ID_RESULT_MAPPINGS, resultMap.getIdResultMappings());
    map.put(MAPPED_COLUMNS, resultMap.getMappedColumns());
    map.put(MAPPED_PROPERTIES, resultMap.getMappedProperties());
    map.put(PROPERTY_RESULT_MAPPINGS, resultMap.getPropertyResultMappings());
    map.put(RESULT_MAPPINGS, resultMap.getResultMappings());
    map.put(TYPE, resultMap.getType());
    return map;
  }

  /**
   * 单例的惰性初始化
   */
  private void singleton() {
    if (extendMetaMapperMap == null) {
      synchronized (this) {
        if (extendMetaMapperMap != null) {
          return;
        }
        Map<String, Object> tmpMap = applicationContext.getBeansWithAnnotation(ExtensionMeta.class);
        if (tmpMap == null || tmpMap.size() == 0) {
          extendMetaMapperMap = Collections.emptyMap();
          return;
        }
        extendMetaMapperMap = new HashMap<>((int) (tmpMap.size() / 0.75) + 1);
        for (Map.Entry<String, Object> extendMapper : tmpMap.entrySet()) {
          ExtensionMeta extentionMeta = applicationContext
              .findAnnotationOnBean(extendMapper.getKey(), ExtensionMeta.class);
          // 实体类
          Class entityClass = extentionMeta.entityClazz();
          extendMetaMapperMap
              .put(entityClass.getName(), (IExtendMetaMapper) extendMapper.getValue());
        }
      }
    }
  }

  /**
   * 查询条件动态拼接。用于获取符合条件的记录（可分页）
   *
   * @param condition
   * @param fields
   * @return
   */
  public List<T> selectByDynamicCondition(
      EntityCondition condition, List<String> fields) {

    visitCondition(condition);
    List<String> selectFields = getSelectFields(fields);
    return innerSelect(condition, selectFields);
  }

  public List<T> selectWithRelationByDynamicCondition(EntityCondition condition) {
    if (condition != null && condition.getLimit() != null) {
      throw new RuntimeException("使用主子表联合查询，则查询条件中不能用limit语句！因为，limit语句无法对查询结果进行正确分页，请检查！");
    }
    if (relativeSelectFields != null) {
      visitCondition(condition);
      return innerRelativeSelect(condition, relativeSelectFields);
    }
    List<Object> list = null;
    if (extEntityClassName == null) {
      list = DynamicSqlInjector.RESULTMAP_MAP.get(entityClassName);
    } else {
      list = DynamicSqlInjector.RESULTMAP_MAP.get(extEntityClassName);
    }
    if (CollectionUtils.isEmpty(list)) {
      return this.selectByDynamicCondition(condition, (List) null);
    }
    ResultMap resultMap = (ResultMap) list.get(1);
    List<ResultMapping> resultMappings = resultMap.getResultMappings();
    List<String> selectFields = new ArrayList<>();
    List<ResultMapping> nestedResultMapList = new ArrayList<>();
    for (ResultMapping resultMapping : resultMappings) {
      if (resultMapping.getNestedResultMapId() != null) {
        nestedResultMapList.add(resultMapping);
        continue;
      }
      selectFields.add(String.format("%s", resultMapping.getColumn()));
    }
    List<String> fields = getSelectFields(selectFields);
    selectFields.clear();
    fields.forEach(selectField -> {
      String field = selectField.substring(1, selectField.length() - 1);
      if (parentTableColumnSet != null && parentTableColumnSet.contains(field)) {
        selectFields.add("t_s0." + selectField);
      } else if (selfTableColumnSet != null && selfTableColumnSet.contains(field)) {
        selectFields.add("t_s1." + selectField);
      } else {
        selectFields.add("t0." + selectField);
      }
    });
    if (!CollectionUtils.isEmpty(nestedResultMapList)) {
      for (ResultMapping nested : nestedResultMapList) {
        ResultMap subResultMap = (ResultMap) DynamicSqlInjector.configuration
            .getResultMap(nested.getNestedResultMapId());
        List<ResultMapping> resultmappings = subResultMap.getResultMappings();
        for (ResultMapping resultMapping : resultmappings) {
          String origColumn = resultMapping.getColumn();
          String column = nested.getColumnPrefix() + origColumn;
          selectFields.add(String.format("t1.`%s` as `%s`", origColumn, column));
        }
      }
    }
    relativeSelectFields = selectFields;
    visitCondition(condition);
    return innerRelativeSelect(condition, relativeSelectFields);
  }

  /**
   * 查询条件动态拼接。用于获取符合条件的记录（可分页）
   *
   * @param condition
   * @return
   */
  public List<T> selectByDynamicCondition(
      EntityCondition condition) {

    visitCondition(condition);
    return innerSelect(condition, null);
  }


  /**
   * 查询条件动态拼接。用于获取符合条件的记录总数
   *
   * @param condition
   * @return
   */
  public int countAllByDynamicCondition(
      EntityCondition condition) {

    Map<String, Object> map = new TreeMap<>();
    visitCondition(condition);
    // 检查分页查询删除limit语句
    MybatisCountAllSqlVisitor visitor = new MybatisCountAllSqlVisitor();
    visitor.visit(condition);
    // 对于本次分页查询，获取其查询的总数。
    map.put("conditionSql", visitor.getSql());
    // 本次查询生成的sql中，包含的mybatis变量。
    map.putAll(visitor.getMybatisParamMap());
    if (extendMapper != null) {
      return extendMapper.countAllByDynamicCondition(map);
    }
    return ((IMetaMapper) mapper).countAllByDynamicCondition(map);
  }


  /**
   * 查询条件动态拼接。用于获取符合条件的记录（可分页）
   *
   * @param condition
   * @param field
   * @return
   */
  public List<T> selectByDynamicCondition(
      EntityCondition condition, @GraphQLEnvironment graphql.language.Field field) {

    visitCondition(condition);
    List<String> selectFields = getSelectFields(field);
    return innerSelect(condition, selectFields);
  }

  /**
   * 根据当前层次传入的field列表，返回需要从数据库表中查询出的实际field列表。 由于@Batched/@GraphQLContext等注解的因素，从前端传入的当前层的查询字段，可能不全是数据库中的表字段。
   * 也可能是逻辑字段。因此，需要在此处进行过滤/转换。
   *
   * @param fieldList
   * @return
   */
  @Override
  public List<String> getSelectFields(List<String> fieldList) {

    List<String> resultList = new ArrayList<>(fieldList.size());
    fieldList.forEach(field -> {
      String targetField = column2PropertyMap.get(field);
      if (targetField != null) {
        resultList.add(String.format("`%s`", field));
        return;
      }
      targetField = property2ColumnMap.get(field);
      if (targetField != null) {
        resultList.add(String.format("`%s`", targetField));
      }
    });
    /**
     * 如果外层无从数据库中查询的字段，
     * 则使用id字段或property2ColumnMap中任取一个value
     */
    if (resultList.isEmpty()) {
      return Arrays.asList(keyColumn);
    }
    return resultList;
  }

  /**
   * 根据返回结果List转换为与stream流相同大小的List<List>
   *
   * @param tList
   * @param function
   * @param stream
   * @param <T>
   * @param <R>
   * @return
   */
  public <T, R> List<List<T>> getSubFieldListOfList(List<T> tList, Function<T, R> function,
      Stream<R> stream) {
    Map<R, List<T>> map = tList.stream().collect(Collectors.groupingBy(function));
    List<List<T>> resultList = new LinkedList<>();
    stream.forEach(robj -> resultList.add(map.getOrDefault(robj, Collections.emptyList())));
    return resultList;
  }

  /**
   * 获取数据库的column字段所对应的java实体属性字段
   *
   * @param column
   * @return
   */
  public String getPropertyOfColumn(String column) {

    return this.column2PropertyMap.get(column);
  }

  /**
   * 获取当前实体对应的数据库主键列名
   *
   * @return
   */
  public String getKeyColumn() {

    return keyColumn;
  }

  /**
   * 获取当前实体对应的java property名
   *
   * @return
   */
  public String getKeyProperty() {

    return keyProperty;
  }

  @Override
  public String getTableName() {

    return tableName;
  }

  public String getParentTableName() {

    return parentTableName;
  }

  /**
   * 根据返回结果List转换为与stream流相同大小的List
   *
   * @param tList
   * @param function
   * @param stream
   * @param <T>
   * @param <R>
   * @return
   */
  public <T, R> List<T> getSubFieldList(List<T> tList, Function<T, R> function, Stream<R> stream) {
    List<T> resultList = new LinkedList<>();
    Map<R, T> map = tList.stream().collect(Collectors.toMap(function, t -> t));
    stream.forEach(robj -> resultList.add(map.getOrDefault(robj, null)));
    return resultList;
  }

  @Transactional(rollbackFor = Exception.class)
  public int insert(T entity) {

    // 添加validator校验
    this.validateInsertEntity(entity);
    if (entity == null) {
      return 0;
    }
    String inputClassName = entity.getClass().getName();
    // 插主表
    int result = mapper.insert(entity);
    // 插扩展表
    if (!Objects.equals(inputClassName, entityClassName) && parentKeyField != null) {
      Object value = parentKeyFieldAccess.get(entity, parentKeyFieldIndex);
      keyFieldAccess.set(entity, keyFieldIndex, value);
      extendMapper.insert(entity);
    }
    return result;
  }

  protected void deleteByIdCheck(Serializable id) {
    if (id == null) {
      throw new RuntimeException("根据id删除数据，id不能为空！");
    }
  }

  public int deleteById(Serializable id) {
    deleteByIdCheck(id);
    if (extendMapper != null) {
      return extendMapper.deleteById(id);
    }
    return mapper.deleteById(id);
  }

  protected void deleteByMapCheck(Map<String, Object> columnMap) {
    if (CollectionUtils.isEmpty(columnMap)) {
      throw new RuntimeException("使用deleteByMap，传参不能为空Map!");
    }
    for (Map.Entry<String, Object> entry : columnMap.entrySet()) {
      if (entry.getKey() == null || entry.getValue() == null) {
        throw new RuntimeException(
            String.format("deleteByMap传入的map，存在元素的key为null或value为null，请检查。map: %s",
                JSON.toJSONString(columnMap)));
      }
    }
  }

  public int deleteByMap(Map<String, Object> columnMap) {
    deleteByMapCheck(columnMap);
    if (extendMapper != null) {
      return extendMapper.deleteByMap(columnMap);
    }
    return mapper.deleteByMap(columnMap);
  }

  protected void deleteByWrapperCheck(Wrapper<T> wrapper) {
    if (wrapper == null) {
      throw new RuntimeException("拼接wrapper删除数据，从安全角度考虑，wrapper不能为空！");
    }
    if (wrapper.getEntity() == null && StringUtils.isEmpty(wrapper.getSqlSegment())) {
      throw new RuntimeException("拼接删除条件的wrapper，传入的wrapper并未添加任何删除条件，会清空表中所有数据。请检查！");
    }
  }

  public int delete(Wrapper<T> wrapper) {
    deleteByWrapperCheck(wrapper);
    if (extendMapper != null) {
      return extendMapper.delete(wrapper);
    }
    return mapper.delete(wrapper);
  }

  protected void deleteBatchIdsCheck(Collection<? extends Serializable> idList) {
    // 根据id列表删除数据
    if (CollectionUtils.isEmpty(idList)) {
      throw new RuntimeException("根据id列表删除数据，id列表不能为空。idList: " + idList);
    }
    int nullSize = 0;
    for (Serializable id : idList) {
      if (id == null) {
        nullSize++;
      }
    }
    if (Objects.equals(nullSize, idList.size())) {
      throw new RuntimeException("根据id列表删除数据，id列表中的数据全部为null，请检查!");
    }
  }

  public int deleteBatchIds(Collection<? extends Serializable> idList) {
    deleteBatchIdsCheck(idList);
    if (extendMapper != null) {
      return extendMapper.deleteBatchIds(idList);
    }
    return mapper.deleteBatchIds(idList);
  }

  public int updateById(T entity) {

    if (entity == null) {
      return 0;
    }
    String inputClassName = entity.getClass().getName();
    if (extendMapper != null && !Objects.equals(inputClassName, entityClassName)) {
      insertExtendEntityBeforeUpdate(entity);
      return extendMapper.updateById(entity);
    }
    return mapper.updateById(entity);
  }

  public int update(T entity, Wrapper<T> updateWrapper) {

    if (entity == null) {
      return 0;
    }
    String inputClassName = entity.getClass().getName();
    if (extendMapper != null && !Objects.equals(inputClassName, entityClassName)) {
      insertExtendEntityBeforeUpdate(entity);
      return extendMapper.update(entity, updateWrapper);
    }
    return mapper.update(entity, updateWrapper);
  }

  private void insertExtendEntityBeforeUpdate(T entity) {
    if (parentKeyField == null) {
      return;
    }
    try {
      // 如果当前update实体存在，且扩展实体不存在，则先新增一次扩展实体。
      Object keyValue = keyFieldAccess.get(entity, keyFieldIndex);
      if (keyValue != null) {
        return;
      }
      // 获取父类主键值，赋值给扩展类。
      Object parentKeyValue = parentKeyFieldAccess.get(entity, parentKeyFieldIndex);
      if (parentKeyValue == null) {
        return;
      }
      keyFieldAccess.set(entity, keyFieldIndex, parentKeyValue);
      extendMapper.insert(entity);
    } catch (Exception e) {
      // 在某些情况下，例如，旧数据可能并不存在扩展表。
      // 但某次更新时，扩展表中的必填字段为空，则会报异常。
      // 这种异常，可能是并不需要报的（旧数据无需进行扩展表维护）。因此此处仅以日志记录。
      log.error("", e);
    }
  }

  public T selectById(Serializable id) {

    if (extendMapper != null) {
      return (T) extendMapper.selectById(id);
    }
    return (T) mapper.selectById(id);
  }

  public List<T> selectBatchIds(Collection<? extends Serializable> idList) {

    // 根据id列表批量查找
    if (CollectionUtils.isEmpty(idList)) {
      return Collections.emptyList();
    }
    if (extendMapper != null) {
      return extendMapper.selectBatchIds(idList);
    }
    List<List<? extends Serializable>> lists = partionCollection(idList);
    // id列表切割。
    List<T> resultList = new LinkedList<>();
    Iterator<List<? extends Serializable>> iterator = lists.iterator();
    while (iterator.hasNext()) {
      List<? extends Serializable> list = iterator.next();
      resultList.addAll(mapper.selectBatchIds(list));
    }
    return resultList;
  }

  public List<T> selectByMap(Map<String, Object> columnMap) {

    if (columnMap == null) {
      columnMap = Collections.emptyMap();
    }
    if (extendMapper != null) {
      return extendMapper.selectByMap(columnMap);
    }
    return mapper.selectByMap(columnMap);
  }

  public T selectOne(Wrapper<T> queryWrapper) {

    if (extendMapper != null) {
      return (T) extendMapper.selectOne(queryWrapper);
    }
    return (T) mapper.selectOne(queryWrapper);
  }

  public Integer selectCount(Wrapper<T> queryWrapper) {

    if (extendMapper != null) {
      return extendMapper.selectCount(queryWrapper);
    }
    return mapper.selectCount(queryWrapper);
  }

  public List<T> selectList(Wrapper<T> queryWrapper) {

    if (extendMapper != null) {
      return extendMapper.selectList(queryWrapper);
    }
    return mapper.selectList(queryWrapper);
  }

  public List<Map<String, Object>> selectMaps(Wrapper<T> queryWrapper) {

    if (extendMapper != null) {
      return extendMapper.selectMaps(queryWrapper);
    }
    return mapper.selectMaps(queryWrapper);
  }

  public List<Object> selectObjs(Wrapper<T> queryWrapper) {

    if (extendMapper != null) {
      return extendMapper.selectObjs(queryWrapper);
    }
    return mapper.selectObjs(queryWrapper);
  }

  public IPage<T> selectPage(IPage<T> page, Wrapper<T> queryWrapper) {

    if (extendMapper != null) {
      return extendMapper.selectPage(page, queryWrapper);
    }
    return mapper.selectPage(page, queryWrapper);
  }

  public IPage<Map<String, Object>> selectMapsPage(IPage<T> page, Wrapper<T> queryWrapper) {

    if (extendMapper != null) {
      return extendMapper.selectMapsPage(page, queryWrapper);
    }
    return mapper.selectMapsPage(page, queryWrapper);
  }

  /**
   * 批量插入要回滚！
   *
   * @param entityList
   * @return
   */
  @Transactional(rollbackFor = Exception.class)
  public int insertBatchSomeColumn(List<T> entityList) {

    if (CollectionUtils.isEmpty(entityList)) {
      return 0;
    }
    entityList.forEach(this::validateInsertEntity);
    List<List<T>> batchList = partionList(entityList);
    int sum = 0;
    // 分批插入
    for (List<T> list : batchList) {
      sum += mapper.insertBatchSomeColumn(list);
    }
    if (parentKeyField != null) {
      for (T entity : entityList) {
        Object value = parentKeyFieldAccess.get(entity, parentKeyFieldIndex);
        keyFieldAccess.set(entity, keyFieldIndex, value);
      }
    }
    String inputClassName = entityList.get(0).getClass().getName();
    if (extendMapper != null && !Objects.equals(inputClassName, entityClassName)) {
      for (List<T> list : batchList) {
        extendMapper.insertBatchSomeColumn(list);
      }
    }
    return sum;
  }

  private void validateInsertEntity(T entity) {
    if (graphqlValidator == null) {
      Validator validator = applicationContext.getBean(Validator.class);
      if (validator != null) {
        graphqlValidator = validator;
      } else {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
            .configure()
            .addProperty(HibernateValidatorConfiguration.FAIL_FAST, "true")
            .buildValidatorFactory();
        graphqlValidator = validatorFactory.getValidator();
      }
    }
    Set<ConstraintViolation<Object>> results = graphqlValidator.validate(entity);
    if (CollectionUtils.isEmpty(results)) {
      return;
    }
    StringBuilder stringBuilder = new StringBuilder(256);
    Iterator<ConstraintViolation<Object>> iterator = results.iterator();
    while (iterator.hasNext()) {
      stringBuilder.append(iterator.next().getMessage());
      if (!iterator.hasNext()) {
        break;
      }
      stringBuilder.append(",");
    }
    throw new RuntimeException(stringBuilder.toString());

  }

  /**
   * 将实体列表根据分批大小进行分割
   *
   * @param entityList
   * @return
   */
  private List<List<T>> partionList(List<T> entityList) {
    if (CollectionUtils.isEmpty(entityList)) {
      return Collections.emptyList();
    }
    if (entityList.size() <= BATCH_SIZE) {
      return Arrays.asList(entityList);
    }
    return Lists.partition(entityList, BATCH_SIZE);
  }

  public List partionCollection(Collection idList) {
    if (CollectionUtils.isEmpty(idList)) {
      return Collections.emptyList();
    }
    if (idList.size() <= BATCH_SIZE) {
      return Arrays.asList(idList);
    }
    List list = null;
    if (idList instanceof List) {
      list = (List) idList;
    } else {
      list = new ArrayList<>(idList);
    }
    int size = list.size();
    int batchNum = (int) Math.ceil((1.0 * size) / BATCH_SIZE);
    List<List> lists = new ArrayList<>(batchNum);
    for (int i = 0; i < batchNum; i++) {
      int start = i * BATCH_SIZE;
      int end = Math.min(((i + 1) * BATCH_SIZE), size);
      lists.add(list.subList(start, end));
    }
    return lists;
  }

  @PostConstruct
  public void postInit() {
    log.info("serviceImpl: {} init method called.",
        this.getClass().getName());
    init();
  }
}
