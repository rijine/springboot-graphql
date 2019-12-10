package com.yonyou.einvoice.common.metadata.graphql.interceptor;

import graphql.AssertException;
import io.leangen.graphql.execution.InvocationContext;
import io.leangen.graphql.execution.ResolverInterceptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 异常拦截器。 GraphQL默认只能拦截其自身定义的异常类型。 若为RuntimeException或其他运行时异常，则需要进行转换。
 */
@Slf4j
@Component
public class EinvoiceGraphQLExceptionInterceptor implements ResolverInterceptor {

  @Override
  public Object aroundInvoke(InvocationContext context, Continuation continuation)
      throws Exception {
    try {
      return continuation.proceed(context);
    } catch (Exception e1) {
      if (e1 instanceof InvocationTargetException) {
        log.error("graphql接口调用的业务service报错：\n",
            ((InvocationTargetException) e1).getTargetException());
        // 转换为graphql异常类型
        throw new AssertException(
            Optional.ofNullable(((InvocationTargetException) e1).getTargetException())
                .orElseGet(() -> e1).getMessage());
      }
      log.error("graphql接口调用的业务service报错：\n", e1.getMessage());
      throw new AssertException(e1.getMessage());
    }
  }

}
