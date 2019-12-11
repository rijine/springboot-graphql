package metadata.base;

import com.yonyou.einvoice.common.metadata.element.Groupby;
import com.yonyou.einvoice.common.metadata.visitor.BaseSqlVisitor;
import metadata.Utils;
import org.junit.Assert;
import org.junit.Test;

public class GroupbyTest {

  @Test
  public void groupbyTest() {
    Groupby groupby = Utils.getTObject("groupby/groupby1.json", Groupby.class);
    BaseSqlVisitor sqlVisitor = new BaseSqlVisitor();
    sqlVisitor.visit(groupby);
    Assert.assertEquals("group by t0.`name`", sqlVisitor.getSql());

    groupby = Utils.getTObject("groupby/groupby2.json", Groupby.class);
    sqlVisitor.reset();
    sqlVisitor.visit(groupby);
    Assert.assertEquals("group by t0.`name`, count(distinct t0.`id`)", sqlVisitor.getSql());

    groupby = Utils.getTObject("groupby/groupby3.json", Groupby.class);
    sqlVisitor.reset();
    sqlVisitor.visit(groupby);
    Assert.assertEquals("group by t0.`name`, t0.`id`", sqlVisitor.getSql());
  }

  @Test(expected = RuntimeException.class)
  public void groupbyException() {
    Groupby groupby = Utils.getTObject("groupby/groupbyexception1.json", Groupby.class);
    BaseSqlVisitor visitor = new BaseSqlVisitor();
    visitor.visit(groupby);
  }

}
