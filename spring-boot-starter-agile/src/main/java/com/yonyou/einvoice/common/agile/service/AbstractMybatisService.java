package com.yonyou.einvoice.common.agile.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.google.common.collect.Lists;
import com.yonyou.einvoice.common.agile.element.EntityCondition;
import com.yonyou.einvoice.common.agile.mp.injector.DynamicSqlInjector;
import com.yonyou.einvoice.common.agile.mp.repository.IMetaMapper;
import com.yonyou.einvoice.common.agile.visitor.MybatisCountAllSqlVisitor;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

@Slf4j
public class AbstractMybatisService<T, Q extends IMetaMapper> extends
    AbstractService<T, Q> {
  /**
   * 分批大小。例如，批量插入时每1000个插一次。 根据id查询实体时，每次查1000个id，然后拼接返回结果。
   */
  private final int BATCH_SIZE = 1000;

  /**
   * bean类型T所对应的数据库表名
   */
  private String tableName;

  /**
   * 主键column
   */
  private String keyColumn;

  /**
   * 主键的java property
   */
  private String keyProperty;

  /**
   * 用于保存T数据类型的 数据库字段 -> java属性字段映射
   */
  private Map<String, String> column2PropertyMap;
  /**
   * 用于保存T数据类型的 java属性字段 -> 数据库字段映射
   */
  private Map<String, String> property2ColumnMap;

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
    firstInit = false;
  }

  /**
   * 查询条件动态拼接。用于获取符合条件的记录（可分页）
   *
   * @param condition
   * @param fields
   * @return
   */
  private List<T> selectByDynamicCondition(
      EntityCondition condition, List<String> fields) {

    visitCondition(condition);
    List<String> selectFields = getSelectFields(fields);
    return innerSelect(condition, selectFields);
  }

  public List<T> selectByDynamicCondition(
      EntityCondition condition, SFunction<T, ?>... sFunctions) {
    if (sFunctions == null || sFunctions.length == 0) {
      throw new RuntimeException("传递selectFields的sFunctions列表不能为空，请检查");
    }
    List<String> fields = new ArrayList<>(sFunctions.length);
    for (SFunction sFunction : sFunctions) {
      String column = EntityCondition.getColumnFromSFunction(sFunction);
      fields.add(column);
    }
    return this.selectByDynamicCondition(condition, fields);
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

  public int insert(T entity) {

    // 添加validator校验
    this.validateInsertEntity(entity);
    if (entity == null) {
      return 0;
    }
    String inputClassName = entity.getClass().getName();
    // 插主表
    int result = mapper.insert(entity);
    return result;
  }

  protected void deleteByIdCheck(Serializable id) {
    if (id == null) {
      throw new RuntimeException("根据id删除数据，id不能为空！");
    }
  }

  public int deleteById(Serializable id) {
    deleteByIdCheck(id);
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
    return mapper.deleteBatchIds(idList);
  }

  public int updateById(T entity) {
    return mapper.updateById(entity);
  }

  public int update(T entity, Wrapper<T> updateWrapper) {
    return mapper.update(entity, updateWrapper);
  }

  public T selectById(Serializable id) {

    return (T) mapper.selectById(id);
  }

  public List<T> selectBatchIds(Collection<? extends Serializable> idList) {

    // 根据id列表批量查找
    if (CollectionUtils.isEmpty(idList)) {
      return Collections.emptyList();
    }
    List<Collection<? extends Serializable>> lists = partionCollection(idList);
    // id列表切割。
    List<T> resultList = new LinkedList<>();
    Iterator<Collection<? extends Serializable>> iterator = lists.iterator();
    while (iterator.hasNext()) {
      Collection<? extends Serializable> list = iterator.next();
      resultList.addAll(mapper.selectBatchIds(list));
    }
    return resultList;
  }

  public List<T> selectByMap(Map<String, Object> columnMap) {

    if (columnMap == null) {
      columnMap = Collections.emptyMap();
    }
    return mapper.selectByMap(columnMap);
  }

  public T selectOne(Wrapper<T> queryWrapper) {
    return (T) mapper.selectOne(queryWrapper);
  }

  public Integer selectCount(Wrapper<T> queryWrapper) {
    return mapper.selectCount(queryWrapper);
  }

  public List<T> selectList(Wrapper<T> queryWrapper) {
    return mapper.selectList(queryWrapper);
  }

  public List<Map<String, Object>> selectMaps(Wrapper<T> queryWrapper) {
    return mapper.selectMaps(queryWrapper);
  }

  public List<Object> selectObjs(Wrapper<T> queryWrapper) {
    return mapper.selectObjs(queryWrapper);
  }

  public IPage<T> selectPage(IPage<T> page, Wrapper<T> queryWrapper) {
    return mapper.selectPage(page, queryWrapper);
  }

  public IPage<Map<String, Object>> selectMapsPage(IPage<T> page, Wrapper<T> queryWrapper) {
    return mapper.selectMapsPage(page, queryWrapper);
  }

  /**
   * 批量插入要回滚！
   *
   * @param entityList
   * @return
   */
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
