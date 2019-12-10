package com.yonyou.einvoice.common.metadata.graphql.context;

/**
 * 用于实现在多线程上下文中传递信息。
 *
 * @author liuqiangm
 */
public interface AbstractAsyncTransmitContext {

  /**
   * 需要从主线程中获取到的上下文信息。
   *
   * @return
   */
  public abstract Object getMasterThreadContext();

  /**
   * 需要在子线程中根据主上下文信息进行的设置。
   *
   * @param context
   * @param fieldName
   */
  public abstract void setSubThreadContext(Object context, String fieldName);

  /**
   * 子线程执行结束后，执行清理工作。 例如，若赋值了上下文信息，则需要在此方法中对上下文进行清理。
   */
  default void resetSubThreadContext() {

  }
}
