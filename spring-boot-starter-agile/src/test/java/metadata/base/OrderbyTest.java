package metadata.base;

import com.yonyou.einvoice.common.metadata.element.Aggr;
import com.yonyou.einvoice.common.metadata.element.Orderby;
import com.yonyou.einvoice.common.metadata.element.Orderbyitem;
import com.yonyou.einvoice.common.metadata.visitor.BaseSqlVisitor;
import com.yonyou.einvoice.common.metadata.visitor.MybatisSqlVisitor;
import metadata.Utils;
import org.junit.Assert;
import org.junit.Test;

public class OrderbyTest {

  @Test
  public void orderbyTest() {
    Orderby orderby = Utils.getTObject("orderby/orderby1.json", Orderby.class);
    BaseSqlVisitor sqlVisitor = new BaseSqlVisitor();
    sqlVisitor.reset();
    sqlVisitor.visit(orderby);
    Assert.assertEquals("order by n asc, t0.`id` desc, sum(t0.`code`) asc, count(t0.`ts`) asc",
        sqlVisitor.getSql());

  }

  @Test(expected = RuntimeException.class)
  public void orderbyitemException() {
    Orderbyitem orderbyitem = new Orderbyitem();
    Aggr aggr = new Aggr();
    orderbyitem.setAggr(aggr);
    MybatisSqlVisitor sqlVisitor = new MybatisSqlVisitor();
    sqlVisitor.visit(orderbyitem);
  }

}
