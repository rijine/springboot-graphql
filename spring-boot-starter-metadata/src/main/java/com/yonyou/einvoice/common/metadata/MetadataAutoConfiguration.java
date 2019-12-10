package com.yonyou.einvoice.common.metadata;

import com.yonyou.einvoice.common.metadata.config.EinvoiceConfig;
import com.yonyou.einvoice.common.metadata.graphiql.giql.OnlineIQLController;
import com.yonyou.einvoice.common.metadata.graphiql.giql.OnlineIQLProperties;
import com.yonyou.einvoice.common.metadata.graphiql.tgiql.TestIQLController;
import com.yonyou.einvoice.common.metadata.graphiql.tgiql.TestIQLProperties;
import com.yonyou.einvoice.common.metadata.graphql.config.GraphQLConfig;
import com.yonyou.einvoice.common.metadata.graphql.interceptor.EinvoiceGraphQLExceptionInterceptor;
import com.yonyou.einvoice.common.metadata.graphql.servlet.GraphQLController;
import com.yonyou.einvoice.common.metadata.graphql.servlet.components.DefaultExecutionInputCustomizer;
import com.yonyou.einvoice.common.metadata.graphql.servlet.components.DefaultExecutionResultHandler;
import com.yonyou.einvoice.common.metadata.graphql.servlet.components.DefaultGraphQLInvocation;
import com.yonyou.einvoice.common.metadata.graphql.servlet.components.FastJsonSerializer;
import com.yonyou.einvoice.common.metadata.mp.injector.DynamicSqlInjector;
import com.yonyou.einvoice.common.metadata.service.GeneralPermissionService;
import com.yonyou.einvoice.common.metadata.service.GeneralServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(DispatcherServlet.class)
@EnableConfigurationProperties({MetadataProperties.class, OnlineIQLProperties.class,
    TestIQLProperties.class})
@Import({EinvoiceGraphQLExceptionInterceptor.class, DynamicSqlInjector.class,
    GeneralServiceImpl.class,
    GeneralPermissionService.class, GraphQLConfig.class, EinvoiceConfig.class,
    DefaultExecutionInputCustomizer.class,
    DefaultExecutionResultHandler.class, DefaultGraphQLInvocation.class, FastJsonSerializer.class,
    DefaultExecutionResultHandler.class,
    GraphQLController.class,
    OnlineIQLController.class, TestIQLController.class})
public class MetadataAutoConfiguration {

}
