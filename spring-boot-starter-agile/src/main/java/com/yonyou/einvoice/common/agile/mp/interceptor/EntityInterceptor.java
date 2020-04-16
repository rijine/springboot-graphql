package com.yonyou.einvoice.common.agile.mp.interceptor;

import com.baomidou.mybatisplus.annotation.TableField;
import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import com.yonyou.einvoice.common.agile.mp.anno.AggDetailIndex;
import com.yonyou.einvoice.common.agile.mp.anno.AggField;
import com.yonyou.einvoice.common.agile.service.CommonEntityDao;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.util.StringUtils;

@Intercepts({@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {
    Statement.class})})
public class EntityInterceptor implements Interceptor {

  protected static final TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();
  protected static final Map<Class, List<ReflectEntity>> reflectMap = new ConcurrentHashMap<>();
  protected static final Map<Class, Supplier> constructorMap = new ConcurrentHashMap<>();

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    Class entityClass = (Class) CommonEntityDao.local.get();
    if (entityClass == null) {
      return invocation.proceed();
    }
    Statement statement = (Statement) invocation.getArgs()[0];
    //获得结果集
    ResultSet resultSet = statement.getResultSet();
    List<Object> resultList = new ArrayList<>();
    while (resultSet.next()) {
      // 从entityClass中获取实体类内每个字段的反射值
      List<ReflectEntity> reflectEntityList = getReflectEntityListFromClass(entityClass);
      Supplier supplier = getEntitySupplierFromClass(entityClass);
      Object entity = supplier.get();
      for (ReflectEntity reflectEntity : reflectEntityList) {
        reflectEntity.process(entity, resultSet);
      }
      resultList.add(entity);
    }
    return resultList;
  }

  @Override
  public Object plugin(Object target) {
    // 读取@Signature中的配置，判断是否需要生成代理类
    if (target instanceof ResultSetHandler) {
      return Plugin.wrap(target, this);
    } else {
      return target;
    }
  }

  @Override
  public void setProperties(Properties properties) {
  }

  private Supplier getEntitySupplierFromClass(Class clazz) {
    if (constructorMap.containsKey(clazz)) {
      return constructorMap.get(clazz);
    }
    try {
      ConstructorAccess constructorAccess = ConstructorAccess.get(clazz);
      Constructor constructor = clazz.getConstructor();
      constructor.setAccessible(true);
      constructorMap.put(clazz, () -> {
        try {
          return constructorAccess.newInstance();
        } catch (Exception e1) {
          try {
            return constructor.newInstance();
          } catch (Exception e2) {
            e2.printStackTrace();
            return null;
          }
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
    return constructorMap.get(clazz);
  }

  private List<ReflectEntity> getReflectEntityListFromClass(Class clazz) {
    List<ReflectEntity> resultList = reflectMap.get(clazz);
    if (resultList != null) {
      return resultList;
    }
    List<Field> fieldList = getAllFields(clazz);
    resultList = new ArrayList<>(fieldList.size());
    constructAndAddReflectEntity(fieldList, clazz, resultList);
    reflectMap.put(clazz, resultList);
    return resultList;
  }

  private void constructAndAddReflectEntity(List<Field> fieldList, Class clazz,
      List<ReflectEntity> resultList) {
    MethodAccess methodAccess = MethodAccess.get(clazz);
    for (Field field : fieldList) {
      // final修饰的字段需要忽略
      if (Modifier.isFinal(field.getModifiers())) {
        continue;
      }
      String propertyName = field.getName();
      Class propertyType = field.getType();
      ReflectEntity reflectEntity = new ReflectEntity();
      reflectEntity.setField(field);
      reflectEntity.setPropertyName(propertyName);
      reflectEntity.setPropertyType(propertyType);
      AggField aggField = field.getAnnotation(AggField.class);
      if (aggField != null) {
        processAggField(reflectEntity, field, aggField, methodAccess, resultList);
        continue;
      }
      processNotAggField(reflectEntity, field, propertyName, methodAccess, resultList);
    }
  }

  private void processNotAggField(ReflectEntity reflectEntity, Field field, String propertyName,
      MethodAccess methodAccess, List<ReflectEntity> resultList) {
    TableField tableField = field.getAnnotation(TableField.class);
    if (tableField != null && !tableField.exist()) {
      return;
    }
    if (tableField != null && !StringUtils.isEmpty(tableField.value())) {
      reflectEntity.setColumnName(tableField.value());
    } else {
      reflectEntity
          .setColumnName(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, propertyName));
    }
    reflectEntity
        .setPropertyValueAssigner(getPropertyValueAssigner(propertyName, field, methodAccess));
    resultList.add(reflectEntity);
  }

  private BiConsumer<Object, Object> getPropertyValueAssigner(String propertyName, Field field,
      MethodAccess methodAccess) {
    // 优先使用ReflectAsm框架，回调set方法
    try {
      String setMethodName =
          "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
      final int index = methodAccess.getIndex(setMethodName);
      return (instace, val) -> {
        methodAccess.invoke(instace, index, val);
      };
    } catch (Exception e) {
      // 反射设置实体类型的值
      return (instance, val) -> {
        try {
          field.set(instance, val);
        } catch (IllegalAccessException e1) {
          e1.printStackTrace();
        }
      };
    }
  }

  private void processAggField(ReflectEntity reflectEntity, Field field, AggField aggField,
      MethodAccess methodAccess, List<ReflectEntity> resultList) {
    Class aggFieldType = field.getType();
    Supplier aggFieldSupplier = getEntitySupplierFromClass(aggFieldType);
    reflectEntity.setAggFieldSupplier(aggFieldSupplier);
    List<ReflectEntity> reflectEntityList = getReflectEntityListFromClass(aggFieldType);
    List<ReflectEntity> aggReflectEntityList = reflectEntityList.stream()
        .map(ReflectEntity::cloneReflectEntity).collect(
            Collectors.toList());
    // 如果设置了aggPrefix，则需要和AggDetailIndex一起使用。否则，直接去AggDetail实体的字段为数据库字段进行映射
    if (aggField.aggPrefix() != null && !"".equals(aggField.aggPrefix())) {
      String aggPrefix = aggField.aggPrefix();
      for (ReflectEntity aggReflectEntity : aggReflectEntityList) {
        AggDetailIndex aggDetailIndex = aggReflectEntity.getField()
            .getAnnotation(AggDetailIndex.class);
        if (aggDetailIndex == null) {
          throw new RuntimeException(
              "aggDetailIndex of aggFieldType: " + aggFieldType.getName() + " cannot be null");
        }
        String index = aggDetailIndex.aggIndex();
        String column = aggPrefix + index;
        aggReflectEntity.setColumnName(column);
      }
    } else {
      String aggPrefix = field.getName();
      for (ReflectEntity aggReflectEntity : aggReflectEntityList) {
        AggDetailIndex aggDetailIndex = aggReflectEntity.getField()
            .getAnnotation(AggDetailIndex.class);
        if (aggDetailIndex != null) {
          String column = aggPrefix + aggDetailIndex.aggIndex();
          aggReflectEntity.setColumnName(column);
        }
      }
    }
    reflectEntity.setAggDetailReflectEntityList(aggReflectEntityList);
    reflectEntity.setPropertyValueAssigner(
        getPropertyValueAssigner(reflectEntity.getPropertyName(), field, methodAccess));
    resultList.add(reflectEntity);
  }

  private List<Field> getAllFields(Class clazz) {
    List<Field> resultList = new ArrayList<>();
    Class curClass = clazz;
    while (curClass != null && !Objects.equal(curClass, Object.class)) {
      resultList.addAll(Arrays.asList(curClass.getDeclaredFields()));
      curClass = curClass.getSuperclass();
    }
    resultList.forEach(field -> field.setAccessible(true));
    return resultList;
  }
}