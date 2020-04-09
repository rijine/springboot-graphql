package com.yonyou.einvoice.common.agile.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.base.CaseFormat;
import org.springframework.util.StringUtils;

/**
 * Agile框架中的ORM实体所需事先的接口
 */
public interface IAgileEntity {

  /**
   * 获取当前的实体对应的数据库表名
   * 如果指定了@TableName注解，则直接返回表名。
   * 否则，返回当前类名经过转换后的表名
   * @return
   * @author liuqiangm
   */
  default String getTableName() {
    Class<? extends IAgileEntity> clazz = this.getClass();
    return getTableName(clazz);
  }

  static <T extends IAgileEntity> String getTableName(Class<T> tClass) {
    TableName table = tClass.getAnnotation(TableName.class);
    if (table != null && !StringUtils.isEmpty(table.value())) {
      return table.value();
    }
    String tableName = CaseFormat.UPPER_CAMEL
        .to(CaseFormat.LOWER_UNDERSCORE, tClass.getSimpleName());
    return tableName;
  }

}
