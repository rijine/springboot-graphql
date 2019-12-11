package com.github.einvoice.testcase.einvoicehis.permission;

import com.yonyou.einvoice.common.agile.visitor.AbstractPermissionVisitor;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.stereotype.Component;


/**
 * einvoice_his
 *
 * @author liuqiangm
 */

@Component
public class EinvoiceHisVOPermissionVisitor extends AbstractPermissionVisitor {

  @Override
  public Map<String, Map<String, Object>> getPermissionConditionMap() {
    Map<String, Map<String, Object>> map = new TreeMap<>();
    map.put("einvoice_his", new HashMap<String, Object>() {
      {
        put("tenantid", "jowol828");
      }
    });
    return map;
  }
}