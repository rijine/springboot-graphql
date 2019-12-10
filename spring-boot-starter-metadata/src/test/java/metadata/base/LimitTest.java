package metadata.base;

import com.yonyou.einvoice.common.metadata.element.Limit;
import com.yonyou.einvoice.common.metadata.visitor.BaseSqlVisitor;
import org.junit.Assert;
import org.junit.Test;

public class LimitTest {

  @Test
  public void limitTest() {
    Limit limit = new Limit();
    limit.setSize(15);
    limit.setOffset(100);
    BaseSqlVisitor sqlVisitor = new BaseSqlVisitor();
    sqlVisitor.visit(limit);
    Assert.assertEquals("limit 15 offset 100", sqlVisitor.getSql());
  }

}
