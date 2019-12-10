package com.yonyou.einvoice.common.metadata.visitor;

/**
 * 访问者访问的类型接口
 *
 * @author liuqiangm
 */
public interface IMetaElement {

  default void accept(IVisitor visitor) {
  }
}
