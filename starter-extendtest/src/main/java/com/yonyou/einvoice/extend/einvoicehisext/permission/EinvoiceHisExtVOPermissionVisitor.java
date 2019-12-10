package com.yonyou.einvoice.extend.einvoicehisext.permission;

import com.yonyou.einvoice.common.metadata.visitor.AbstractPermissionVisitor;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;


/**
 * einvoice_his_ext
 *
 * @author liuqiangm
 */

@Component
public class EinvoiceHisExtVOPermissionVisitor extends AbstractPermissionVisitor {


  @Override
  public Map<String, Map<String, Object>> getPermissionConditionMap() {
    Map<String, Map<String, Object>> map = new HashMap<>();
    map.put("einvoice_his_ext", new HashMap<String, Object>() {{
      put("ext_tenantid", "jowol828");
    }});
    return map;
  }
}