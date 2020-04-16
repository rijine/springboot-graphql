package com.yonyou.einvoice.common.agile.element;

import java.util.Map;

public interface ISqlEntity {

  String getSql();

  Map<String, Object> getMybatisParamMap();
}
