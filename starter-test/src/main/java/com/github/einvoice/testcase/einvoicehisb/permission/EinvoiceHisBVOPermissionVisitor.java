package com.github.einvoice.testcase.einvoicehisb.permission;

import com.yonyou.einvoice.common.agile.visitor.AbstractPermissionVisitor;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.stereotype.Component;


/**
 * einvoice_his_b
 *
 * @author liuqiangm
 */

@Component
public class EinvoiceHisBVOPermissionVisitor extends AbstractPermissionVisitor {

  @Override
  public Map<String, Map<String, Object>> getPermissionConditionMap() {
    Map<String, Map<String, Object>> map = new TreeMap<>();
    map.put("einvoice_his_b",
        new HashMap<String, Object>() {
          {
            put("corpid", "jowol828");
          }
        });
    return map;
  }
}