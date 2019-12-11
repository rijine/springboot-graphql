package com.yonyou.einvoice.common.agile.element;

import com.yonyou.einvoice.common.agile.enums.DirectionEnum;
import com.yonyou.einvoice.common.agile.visitor.IMetaElement;
import lombok.Getter;
import lombok.Setter;

/**
 * order by的子项。与Field类似
 */
@Setter
@Getter
public class Orderbyitem implements IMetaElement {

  private String sourceAlias;
  private String field;
  private Aggr aggr;
  private String fieldAlias;
  /**
   * 默认升序
   */
  private DirectionEnum direction = DirectionEnum.ASC;

}
