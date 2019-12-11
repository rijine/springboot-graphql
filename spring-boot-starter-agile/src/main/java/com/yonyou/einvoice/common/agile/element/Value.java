package com.yonyou.einvoice.common.agile.element;

import com.yonyou.einvoice.common.agile.enums.TypeEnum;
import com.yonyou.einvoice.common.agile.visitor.IMetaElement;
import com.yonyou.einvoice.common.agile.visitor.IVisitor;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * 具体的值对象
 */
@Getter
@Setter
public class Value implements IMetaElement {

  /**
   * 值可以是一个新的source（即：子select语句）
   */
  private Source source;
  private List<Object> list;
  private Field field;
  private String val;


  @Override
  public void accept(IVisitor visitor) {
    if (source != null) {
      visitor.visit(source);
    }
  }

  public TypeEnum getType() {
    if (source != null) {
      return TypeEnum.SOURCE;
    }
    if (list != null) {
      return TypeEnum.LIST;
    }
    if (val != null) {
      return TypeEnum.CONSTANT;
    }
    return null;
  }

  /**
   * 获取当前Value所实际存储的值。
   *
   * @return
   */
  public Object getValue() {
    if (source != null) {
      return source;
    }
    if (list != null) {
      return list;
    }
    if (val != null) {
      return val;
    }
    if (field != null) {
      return field;
    }
    return null;
  }
}
