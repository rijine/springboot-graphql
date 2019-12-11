package com.yonyou.einvoice.common.agile.graphql.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yonyou.einvoice.common.agile.MetadataProperties;
import com.yonyou.einvoice.common.agile.graphql.IGraphQLService;
import com.yonyou.einvoice.common.agile.graphql.ITestGQLService;
import com.yonyou.einvoice.common.agile.graphql.interceptor.EinvoiceGraphQLExceptionInterceptor;
import graphql.GraphQL;
import graphql.execution.ExecutionStrategy;
import graphql.execution.preparsed.PreparsedDocumentEntry;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaPrinter;
import io.leangen.graphql.GraphQLSchemaGenerator;
import io.leangen.graphql.execution.ResolverInterceptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * graphql的QL配置类。 配置两个QL，一个用于线上，一个用于调试
 *
 * @author liuqiangm
 */
@Configuration
@Slf4j
@Setter
@Getter
public class GraphQLConfig {

  @Autowired
  private MetadataProperties metadataProperties;

  @Bean
  @SuppressWarnings("all")
  public GraphQLSchema onlineQLSchema(Map<String, IGraphQLService> graphqlServiceMap,
      ResolverInterceptor resolverInterceptor) {
    List<ResolverInterceptor> interceptorList = new ArrayList<>();
    interceptorList.add(new EinvoiceGraphQLExceptionInterceptor());
    List<String> packageList = new ArrayList<>();
    packageList.add("com.yonyou");
    if (StringUtils.isNotBlank(metadataProperties.getBasepackages())) {
      String[] packages = metadataProperties.getBasepackages().split(",");
      packageList.addAll(Arrays.asList(packages));
    }
    if (resolverInterceptor != null) {
      interceptorList.add(resolverInterceptor);
    }
    GraphQLSchema schema = new GraphQLSchemaGenerator()
        .withBasePackages(packageList.toArray(new String[packageList.size()]))
        .withOperationsFromSingletons(graphqlServiceMap.values().toArray())
        .withResolverInterceptors(
            interceptorList.toArray(new ResolverInterceptor[interceptorList.size()]))
        .generate();
    String schemaStr = new SchemaPrinter().print(schema);
    log.info("OnlineQL Schema : \n{} ", schemaStr);
    return schema;
  }

  @Bean
  @SuppressWarnings("all")
  public GraphQLSchema testQLSchema(Map<String, ITestGQLService> testGLQServiceMap,
      ResolverInterceptor resolverInterceptor) {
    List<ResolverInterceptor> interceptorList = new ArrayList<>();
    interceptorList.add(new EinvoiceGraphQLExceptionInterceptor());
    List<String> packageList = new ArrayList<>();
    packageList.add("com.yonyou");
    if (StringUtils.isNotBlank(metadataProperties.getBasepackages())) {
      String[] packages = metadataProperties.getBasepackages().split(",");
      packageList.addAll(Arrays.asList(packages));
    }
    if (resolverInterceptor != null) {
      interceptorList.add(resolverInterceptor);
    }
    GraphQLSchema schema = new GraphQLSchemaGenerator()
        .withBasePackages(packageList.toArray(new String[packageList.size()]))
        .withOperationsFromSingletons(testGLQServiceMap.values().toArray())
        .withResolverInterceptors(
            interceptorList.toArray(new ResolverInterceptor[interceptorList.size()]))
        .generate();
    String schemaStr = new SchemaPrinter().print(schema);
    log.info("testQL Schema : \n{} ", schemaStr);
    return schema;
  }

  @Bean
  public GraphQL onlineQL(@Qualifier("onlineQLSchema") GraphQLSchema graphQLSchema,
      ExecutionStrategy executionStrategy) {
    Cache<String, PreparsedDocumentEntry> cache = Caffeine.newBuilder().maximumSize(10_000).build();
    return GraphQL.newGraphQL(graphQLSchema)
        .queryExecutionStrategy(executionStrategy)
        .preparsedDocumentProvider(cache::get)
        .build();
  }

  @Bean
  public GraphQL testQL(@Qualifier("testQLSchema") GraphQLSchema graphQLSchema,
      ExecutionStrategy executionStrategy) {
    Cache<String, PreparsedDocumentEntry> cache = Caffeine.newBuilder().maximumSize(10_000).build();
    return GraphQL.newGraphQL(graphQLSchema)
        .queryExecutionStrategy(executionStrategy)
        .preparsedDocumentProvider(cache::get)
        .build();
  }
}
