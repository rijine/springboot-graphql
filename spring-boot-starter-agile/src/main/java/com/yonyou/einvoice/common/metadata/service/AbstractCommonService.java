package com.yonyou.einvoice.common.metadata.service;

import com.yonyou.einvoice.common.metadata.visitor.AbstractPermissionVisitor;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.springframework.context.ApplicationContext;

/**
 * 公共service服务，用于添加权限visitor获取方法。
 *
 * @author liuqiangm
 */
public class AbstractCommonService {

  protected Collection<AbstractPermissionVisitor> permissionVisitors;

  /**
   * 用于从spring容器中获取到当前所注入的权限visitor列表
   *
   * @param applicationContext
   * @return
   */
  protected Collection<AbstractPermissionVisitor> getPermissionVisitorList(
      ApplicationContext applicationContext) {
    // 通过执行permissionVisitor方法，进行遍历，添加权限控制
    if (permissionVisitors != null) {
      return permissionVisitors;
    }
    Map<String, AbstractPermissionVisitor> permissionVisitorMap = applicationContext
        .getBeansOfType(AbstractPermissionVisitor.class);
    if (permissionVisitorMap == null || permissionVisitorMap.size() == 0) {
      permissionVisitors = Collections.emptyList();
      return permissionVisitors;
    }
    permissionVisitors = permissionVisitorMap.values();
    return permissionVisitors;
  }

}
