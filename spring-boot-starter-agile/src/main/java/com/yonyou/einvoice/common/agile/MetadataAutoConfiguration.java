package com.yonyou.einvoice.common.agile;

import com.yonyou.einvoice.common.agile.config.EinvoiceConfig;
import com.yonyou.einvoice.common.agile.graphiql.giql.OnlineIQLController;
import com.yonyou.einvoice.common.agile.graphiql.giql.OnlineIQLProperties;
import com.yonyou.einvoice.common.agile.graphiql.tgiql.TestIQLController;
import com.yonyou.einvoice.common.agile.graphiql.tgiql.TestIQLProperties;
import com.yonyou.einvoice.common.agile.graphql.config.GraphQLConfig;
import com.yonyou.einvoice.common.agile.graphql.interceptor.EinvoiceGraphQLExceptionInterceptor;
import com.yonyou.einvoice.common.agile.graphql.servlet.GraphQLController;
import com.yonyou.einvoice.common.agile.graphql.servlet.components.DefaultExecutionInputCustomizer;
import com.yonyou.einvoice.common.agile.graphql.servlet.components.DefaultExecutionResultHandler;
import com.yonyou.einvoice.common.agile.graphql.servlet.components.DefaultGraphQLInvocation;
import com.yonyou.einvoice.common.agile.graphql.servlet.components.FastJsonSerializer;
import com.yonyou.einvoice.common.agile.mp.injector.DynamicSqlInjector;
import com.yonyou.einvoice.common.agile.service.GeneralServiceImpl;
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
    GeneralServiceImpl.class, GraphQLConfig.class, EinvoiceConfig.class,
    DefaultExecutionInputCustomizer.class,
    DefaultExecutionResultHandler.class, DefaultGraphQLInvocation.class, FastJsonSerializer.class,
    DefaultExecutionResultHandler.class,
    GraphQLController.class,
    OnlineIQLController.class, TestIQLController.class})
public class MetadataAutoConfiguration {

}
