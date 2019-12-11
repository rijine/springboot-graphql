package com.yonyou.einvoice.common.agile.service;

import com.yonyou.einvoice.common.agile.element.Aggr;
import com.yonyou.einvoice.common.agile.element.Entity;
import com.yonyou.einvoice.common.agile.element.EntityCondition;
import com.yonyou.einvoice.common.agile.element.Field;
import com.yonyou.einvoice.common.agile.element.Fields;
import com.yonyou.einvoice.common.agile.element.Source;
import com.yonyou.einvoice.common.agile.entity.SqlEntity;
import com.yonyou.einvoice.common.agile.enums.AggrEnum;
import com.yonyou.einvoice.common.agile.graphql.ITestGQLService;
import com.yonyou.einvoice.common.agile.visitor.MybatisCountAllSqlVisitor;
import com.yonyou.einvoice.common.agile.visitor.MybatisSqlVisitor;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * generalService 用于纯通过前端拼接查询条件，生成完整sql，并返回执行结果
 */
@Service
public class GeneralServiceImpl extends AbstractCommonService implements ITestGQLService {

  @GraphQLQuery(name = "selectDynamic", description = "纯通过前端拼接查询条件，生成查询sql（可分页）")
  public SqlEntity selectDynamic(@GraphQLArgument(name = "tableName") String tableName,
      @GraphQLArgument(name = "selectFields") List<String> selectFields,
      @GraphQLArgument(name = "sourceCondition") EntityCondition sourceCondition) {
    Source source = getSourceFromArguments(tableName, selectFields, sourceCondition);
    return generateDynamicSql(source);
  }

  @GraphQLQuery(name = "selectCountAllDynamic", description = "纯通过前端拼接查询条件，生成查询符合条件记录总数的sql。（分页总数）")
  public SqlEntity selectCountAllDynamic(@GraphQLArgument(name = "tableName") String tableName,
      @GraphQLArgument(name = "selectFields") List<String> selectFields,
      @GraphQLArgument(name = "sourceCondition") EntityCondition sourceCondition) {
    Source source = getCountSourceFromArguments(tableName, sourceCondition);
    return generateDynamicCountSql(source);
  }

  /**
   * 根据参数生成Source
   *
   * @param tableName
   * @param selectFields
   * @param sourceCondition
   * @return
   */
  protected Source getSourceFromArguments(String tableName, List<String> selectFields,
      EntityCondition sourceCondition) {
    Source source = new Source();
    Fields fields = new Fields();
    List<Field> fieldList = new ArrayList();
    for (String selectField : selectFields) {
      Field field = new Field();
      field.setField(selectField);
      field.setSourceAlias("t0");
      fieldList.add(field);
    }
    fields.setFieldList(fieldList);
    copy(sourceCondition, source, fields, tableName);
    return source;
  }

  /**
   * 根据参数生成查询总数的Source
   *
   * @param tableName
   * @param sourceCondition
   * @return
   */
  protected Source getCountSourceFromArguments(String tableName, EntityCondition sourceCondition) {
    Source source = new Source();
    Fields fields = new Fields();
    Field field = new Field();
    Aggr aggr = new Aggr();
    aggr.setAggrEnum(AggrEnum.COUNT);
    field.setField("id");
    field.setSourceAlias("t0");
    field.setAggr(aggr);
    fields.setFieldList(Arrays.asList(field));
    copy(sourceCondition, source, fields, tableName);
    source.setLimit(null);
    return source;
  }

  private void copy(EntityCondition sourceCondition, Source source, Fields fields,
      String tableName) {
    Entity entity = new Entity();
    entity.setSource(tableName);
    entity.setAlias("t0");
    entity.setJoins(sourceCondition.getJoins());
    source.setFields(fields);
    source.setEntity(entity);
    source.setConditions(sourceCondition.getConditions());
    source.setHaving(sourceCondition.getHaving());
    source.setGroupby(sourceCondition.getGroupby());
    source.setOrderby(sourceCondition.getOrderby());
    source.setLimit(sourceCondition.getLimit());
  }

  /**
   * 根据Source生成sql
   *
   * @param source
   * @return
   */
  protected SqlEntity generateDynamicSql(@GraphQLArgument(name = "source") Source source) {
    MybatisSqlVisitor visitor = new MybatisSqlVisitor();
    visitor.visit(source);
    SqlEntity sqlEntity = new SqlEntity();
    sqlEntity.setSql(visitor.getSql());
    sqlEntity.setParamMap(visitor.getMybatisParamMap());
    return sqlEntity;
  }

  /**
   * 根据Source生成查询总数sql
   *
   * @param source
   * @return
   */
  protected SqlEntity generateDynamicCountSql(@GraphQLArgument(name = "source") Source source) {
    MybatisCountAllSqlVisitor visitor = new MybatisCountAllSqlVisitor();
    visitor.visit(source);
    SqlEntity sqlEntity = new SqlEntity();
    sqlEntity.setSql(visitor.getSql());
    sqlEntity.setParamMap(visitor.getMybatisParamMap());
    return sqlEntity;
  }
}
