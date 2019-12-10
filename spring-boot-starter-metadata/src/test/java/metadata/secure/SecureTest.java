package metadata.secure;

import com.yonyou.einvoice.common.metadata.element.Condition;
import com.yonyou.einvoice.common.metadata.element.Field;
import com.yonyou.einvoice.common.metadata.visitor.SecureVisitor;
import org.junit.Test;

public class SecureTest {

  SecureVisitor secureVisitor = new SecureVisitor();

  @Test(expected = RuntimeException.class)
  public void strStartTest() {
    Condition condition = new Condition();
    Field field = new Field();
    field.setField("12c");
    condition.setSourceField(field);
    secureVisitor.visit(condition);
  }

  @Test
  public void passTest() {
    Condition condition = new Condition();
    Field field = new Field();
    field.setField("`_se_gd`");
    condition.setSourceField(field);
    secureVisitor.visit(condition);
  }

  @Test(expected = RuntimeException.class)
  public void exprTest() {
    Field field = new Field();
    field.setExpr("select 1");
    secureVisitor.visit(field);
  }
}
