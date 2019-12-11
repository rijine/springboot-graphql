package com.yonyou.einvoice.common.metadata.graphql.strategy;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工厂。 用于标记graphql执行时的线程名称
 *
 * @author liuqiangm
 */
public class GraphQLThreadFactory implements ThreadFactory {

  private final ThreadGroup group;
  private final AtomicInteger threadNumber = new AtomicInteger(1);
  private final String namePrefix;

  public GraphQLThreadFactory() {
    SecurityManager s = System.getSecurityManager();
    group = (s != null) ? s.getThreadGroup() :
        Thread.currentThread().getThreadGroup();
    namePrefix = "graphql" +
        "-exec-";
  }

  @Override
  public Thread newThread(Runnable runnable) {
    Thread t = new Thread(group, runnable,
        namePrefix + threadNumber.getAndIncrement(),
        0);
    if (t.isDaemon()) {
      t.setDaemon(false);
    }
    if (t.getPriority() != Thread.NORM_PRIORITY) {
      t.setPriority(Thread.NORM_PRIORITY);
    }
    return t;
  }
}
