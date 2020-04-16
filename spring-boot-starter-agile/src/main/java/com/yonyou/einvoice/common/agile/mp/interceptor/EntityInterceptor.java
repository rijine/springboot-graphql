package com.yonyou.einvoice.common.agile.mp.interceptor;

import com.baomidou.mybatisplus.annotation.TableField;
import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
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
import java.util.function.Supplier;
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

  TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();

  private static final Map<Class, List<ReflectEntity>> reflectMap = new ConcurrentHashMap<>();
  private static final Map<Class, Supplier> constructorMap = new ConcurrentHashMap<>();

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
        String columnName = reflectEntity.getColumnName();
        Class propertyType = reflectEntity.getPropertyType();
        try {
          Object val = typeHandlerRegistry.getTypeHandler(propertyType)
              .getResult(resultSet, columnName);
          reflectEntity.getPropertyValueConsumer().accept(entity, val);
        } catch (Exception e) {
          e.printStackTrace();
        }
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
    List<ReflectEntity> result = reflectMap.get(clazz);
    if (result != null) {
      return result;
    }
    List<Field> fieldList = getAllFields(clazz);
    result = new ArrayList<>(fieldList.size());
    MethodAccess methodAccess = MethodAccess.get(clazz);
    for (Field field : fieldList) {
      // final修饰的字段需要忽略
      if (Modifier.isFinal(field.getModifiers())) {
        continue;
      }
      String propertyName = field.getName();
      Class propertyType = field.getType();
      ReflectEntity reflectEntity = new ReflectEntity();
      reflectEntity.setPropertyName(propertyName);
      reflectEntity.setPropertyType(propertyType);
      TableField tableField = field.getAnnotation(TableField.class);
      if (tableField != null && !tableField.exist()) {
        continue;
      }
      if (tableField != null && !StringUtils.isEmpty(tableField.value())) {
        reflectEntity.setColumnName(tableField.value());
      } else {
        reflectEntity
            .setColumnName(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, propertyName));
      }
      // 优先使用ReflectAsm框架，回调set方法
      try {
        String setMethodName =
            "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        final int index = methodAccess.getIndex(setMethodName);
        reflectEntity.setPropertyValueConsumer((instace, val) -> {
          methodAccess.invoke(instace, index, val);
        });
      } catch (Exception e) {
        // 反射设置实体类型的值
        reflectEntity.setPropertyValueConsumer((instance, val) -> {
          try {
            field.set(instance, val);
          } catch (IllegalAccessException e1) {
            e1.printStackTrace();
          }
        });
      }
      result.add(reflectEntity);
    }
    return result;
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