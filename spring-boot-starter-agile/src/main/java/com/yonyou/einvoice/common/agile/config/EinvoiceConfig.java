package com.yonyou.einvoice.common.agile.config;

import com.yonyou.einvoice.common.agile.graphql.strategy.BatchedAsyncExecutionStrategy;
import com.yonyou.einvoice.common.agile.service.AbstractMybatisService;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.util.CollectionUtils;

/**
 * 用于配置实际执行query的查询策略。 采用batch+异步结合的执行策略
 *
 * @author liuqiangm
 */
@Configuration
@Slf4j
public class EinvoiceConfig {

  @Bean
  public BatchedAsyncExecutionStrategy getStrategy() {
    BatchedAsyncExecutionStrategy batchedAsyncExecutionStrategy = new BatchedAsyncExecutionStrategy();
    batchedAsyncExecutionStrategy.setAsyncTransmitContext(null);
    return batchedAsyncExecutionStrategy;
  }

  @EventListener
  public void eventListener(ContextRefreshedEvent event) {
    if (event.getApplicationContext().getParent() == null) {
      ApplicationContext applicationContext = event.getApplicationContext();
      Map<String, AbstractMybatisService> serviceMap = applicationContext
          .getBeansOfType(AbstractMybatisService.class);
      if (!CollectionUtils.isEmpty(serviceMap)) {
        serviceMap.values().forEach(abstractMybatisService -> {
          log.info("serviceImpl: {} init method called.",
              abstractMybatisService.getClass().getName());
          abstractMybatisService.init();
        });
      }
    }
  }
}
