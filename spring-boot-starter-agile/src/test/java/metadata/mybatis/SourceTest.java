package metadata.mybatis;

import com.alibaba.fastjson.JSON;
import com.yonyou.einvoice.common.metadata.element.EntityCondition;
import com.yonyou.einvoice.common.metadata.element.Source;
import com.yonyou.einvoice.common.metadata.visitor.MybatisSqlVisitor;
import metadata.Utils;
import org.junit.Assert;
import org.junit.Test;

public class SourceTest {

  @Test
  public void sourceTest() {
    Source source = Utils.getTObject("source/source2.json", Source.class);
    MybatisSqlVisitor sqlVisitor = new MybatisSqlVisitor();
    sqlVisitor.visit(source);
    Assert.assertEquals(
        "select sum(distinct t0.`id`) as s from invoice t0 where t0.`name` = #{_p1} order by t0.`id` asc",
        sqlVisitor.getSql());
    Assert.assertEquals("{_p1=einvoice}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test
  public void sourceConditionTest() {
    EntityCondition source = Utils.getTObject("source/source3.json", EntityCondition.class);
    MybatisSqlVisitor sqlVisitor = new MybatisSqlVisitor();
    sqlVisitor.visit(source);
    System.out.println(JSON.toJSONString(source));
    Assert.assertEquals(
        "inner join invoice t1 on t0.`id` = t1.`id` where t0.`name` = #{_p1} having t0.`name` = #{_p2} order by t0.`id` asc",
        sqlVisitor.getSql());
    Assert.assertEquals("{_p2=einvoice, _p1=einvoice}", sqlVisitor.getMybatisParamMap().toString());
  }
}
