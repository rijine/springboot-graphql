package metadata.mybatis;

import com.yonyou.einvoice.common.metadata.element.Value;
import com.yonyou.einvoice.common.metadata.visitor.MybatisSqlVisitor;
import metadata.Utils;
import org.junit.Assert;
import org.junit.Test;

public class ValueTest {

  @Test
  public void valueTest() {
    Value value = Utils.getTObject("value/value1.json", Value.class);
    MybatisSqlVisitor sqlVisitor = new MybatisSqlVisitor();
    sqlVisitor.visit(value);
    Assert.assertEquals(
        "(#{_p1},#{_p2},#{_p3},#{_p4},#{_p5})",
        sqlVisitor.getSql());
    Assert.assertEquals("{_p2=2, _p1=1, _p4=4, _p3=3, _p5=5}",
        sqlVisitor.getMybatisParamMap().toString());

    value = Utils.getTObject("value/value2.json", Value.class);
    sqlVisitor.reset();
    sqlVisitor.visit(value);
    Assert.assertEquals("{_p2=b, _p1=a, _p4=d, _p3=c, _p5=e}",
        sqlVisitor.getMybatisParamMap().toString());
    Assert.assertEquals(
        "(#{_p1},#{_p2},#{_p3},#{_p4},#{_p5})",
        sqlVisitor.getSql());
  }

}
