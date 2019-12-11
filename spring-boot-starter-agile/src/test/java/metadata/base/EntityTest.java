package metadata.base;

import com.yonyou.einvoice.common.metadata.element.Entity;
import com.yonyou.einvoice.common.metadata.element.Join;
import com.yonyou.einvoice.common.metadata.element.On;
import com.yonyou.einvoice.common.metadata.visitor.BaseSqlVisitor;
import com.yonyou.einvoice.common.metadata.visitor.MybatisSqlVisitor;
import metadata.Utils;
import org.junit.Assert;
import org.junit.Test;

public class EntityTest {

  @Test
  public void onTest() {
    On on = Utils.getTObject("entity/on1.json", On.class);
    BaseSqlVisitor sqlVisitor = new BaseSqlVisitor();
    sqlVisitor.visit(on);
    Assert.assertEquals("on t0.`order` = t1.`id`", sqlVisitor.getSql());
  }

  @Test
  public void joinTest() {
    Join join = Utils.getTObject("entity/join1.json", Join.class);
    MybatisSqlVisitor sqlVisitor = new MybatisSqlVisitor();
    sqlVisitor.visit(join);
    Assert.assertEquals("inner join invoice inv on t0.`order` = inv.`id`",
        sqlVisitor.getSql());
  }

  @Test
  public void entityTest() {
    Entity entity = Utils.getTObject("entity/entity1.json", Entity.class);
    MybatisSqlVisitor visitor = new MybatisSqlVisitor();
    visitor.visit(entity);
    Assert.assertEquals("from invoice t0 inner join invoice inv on t0.`order` = inv.`id`",
        visitor.getSql());
  }

}
