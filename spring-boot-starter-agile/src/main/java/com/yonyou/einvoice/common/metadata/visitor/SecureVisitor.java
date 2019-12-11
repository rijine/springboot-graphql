package com.yonyou.einvoice.common.metadata.visitor;

import com.yonyou.einvoice.common.metadata.element.Condition;
import com.yonyou.einvoice.common.metadata.element.Entity;
import com.yonyou.einvoice.common.metadata.element.Field;
import com.yonyou.einvoice.common.metadata.element.Groupbyitem;
import com.yonyou.einvoice.common.metadata.element.Join;
import com.yonyou.einvoice.common.metadata.element.On;
import com.yonyou.einvoice.common.metadata.element.Orderbyitem;
import org.springframework.util.StringUtils;

/**
 * 安全策略sql。用于对前端拼接的动态查询条件进行初步校验，防止sql注入
 *
 * @author liuqiangm
 */
public class SecureVisitor extends BaseVisitor {

  @Override
  public void visit(Condition condition) {
    super.visit(condition);
  }

  @Override
  public void visit(Entity entity) {
    secureInspect(entity.getAlias());
    super.visit(entity);
  }

  @Override
  public void visit(Field field) {
    secureInspect(field.getSourceAlias());
    secureInspect(field.getField());
    secureInspect(field.getAlias());
    if (field.getExpr() != null) {
      secureInspectExpr(field.getExpr());
    }
    super.visit(field);
  }

  @Override
  public void visit(Groupbyitem groupbyitem) {
    secureInspect(groupbyitem.getSourceAlias());
    secureInspect(groupbyitem.getField());
    secureInspectExpr(groupbyitem.getExpr());
    super.visit(groupbyitem);
  }

  @Override
  public void visit(Join join) {
    secureInspect(join.getAlias());
    super.visit(join);
  }

  @Override
  public void visit(On on) {
    secureInspect(on.getSourceAlias1());
    secureInspect(on.getField1());
    secureInspect(on.getSourceAlias2());
    secureInspect(on.getField2());
    super.visit(on);
  }

  @Override
  public void visit(Orderbyitem orderbyitem) {
    secureInspect(orderbyitem.getSourceAlias());
    secureInspect(orderbyitem.getField());
    secureInspect(orderbyitem.getFieldAlias());
    super.visit(orderbyitem);
  }


  private void secureInspect(String str) {
    if (StringUtils.isEmpty(str)) {
      return;
    }
    char[] chars = str.toCharArray();
    if (chars[0] != '_' && chars[0] != '`' && !isAlphabet(chars[0])) {
      throw new RuntimeException(String.format("字符串%s必须以'_'或字母或'`'开头", str));
    }
    for (int i = 1; i < chars.length; i++) {
      char ch = chars[i];
      boolean inspect = ch == '`' || ch == '_' || ch == '-' || isAlphabet(ch) || isNumeric(ch);
      if (!inspect) {
        throw new RuntimeException(String.format("字符串%s中职能出现五种字符：'`','_','-',字母,数字", str));
      }
    }
  }

  private void secureInspectExpr(String origExpr) {
    if (StringUtils.isEmpty(origExpr)) {
      return;
    }
    String str = origExpr.toLowerCase();
    if (str.contains("select") ||
        str.contains("from") ||
        str.contains("where") ||
        str.contains("update") ||
        str.contains("delete")) {
      throw new RuntimeException(String.format("select语句的字段中不允许出现子sql语句: {}", str));
    }
  }

  private boolean isAlphabet(char ch) {
    boolean lowAlphabet = ch >= 'a' && ch <= 'z';
    boolean highAlphabet = ch >= 'A' && ch <= 'Z';
    return lowAlphabet || highAlphabet;
  }

  private boolean isNumeric(char ch) {
    return ch >= '0' && ch <= '9';
  }


}
