package com.yonyou.einvoice.common.agile.config;

import com.yonyou.einvoice.common.agile.graphql.strategy.BatchedAsyncExecutionStrategy;
import com.yonyou.einvoice.common.agile.mp.interceptor.EntityInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

  @Bean
  public EntityInterceptor entityInterceptor() {
    return new EntityInterceptor();
  }

}
