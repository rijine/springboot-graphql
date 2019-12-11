package com.yonyou.einvoice.common.agile.service;

import com.yonyou.einvoice.common.agile.element.EntityCondition;
import com.yonyou.einvoice.common.agile.element.Source;
import com.yonyou.einvoice.common.agile.entity.SqlEntity;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class GeneralPermissionService extends GeneralServiceImpl {

  @Autowired
  private ApplicationContext applicationContext;


  @Override
  @GraphQLQuery(name = "selectDynamicWithPermission", description = "纯通过前端拼接查询条件，生成包含权限控制的查询sql（可分页）")
  public SqlEntity selectDynamic(@GraphQLArgument(name = "tableName") String tableName,
      @GraphQLArgument(name = "selectFields") List<String> selectFields,
      @GraphQLArgument(name = "sourceCondition") EntityCondition sourceCondition) {
    Source source = getSourceFromArguments(tableName, selectFields, sourceCondition);
    getPermissionVisitorList(applicationContext)
        .forEach(permissionVisitor -> permissionVisitor.visit(source));
    return generateDynamicSql(source);
  }

  @Override
  @GraphQLQuery(name = "selectCountAllDynamicWithPermission", description = "纯通过前端拼接查询条件，生成包含权限控制的符合条件的查记录总数的sql。（分页总数）")
  public SqlEntity selectCountAllDynamic(@GraphQLArgument(name = "tableName") String tableName,
      @GraphQLArgument(name = "selectFields") List<String> selectFields,
      @GraphQLArgument(name = "sourceCondition") EntityCondition sourceCondition) {
    Source source = getCountSourceFromArguments(tableName, sourceCondition);
    getPermissionVisitorList(applicationContext)
        .forEach(permissionVisitor -> permissionVisitor.visit(source));
    return generateDynamicCountSql(source);
  }
}
