package metadata.mybatis;

import com.yonyou.einvoice.common.metadata.element.Condition;
import com.yonyou.einvoice.common.metadata.element.Conditions;
import com.yonyou.einvoice.common.metadata.visitor.MybatisSqlVisitor;
import metadata.Utils;
import org.junit.Assert;
import org.junit.Test;

public class ConditionTest {


  @Test
  public void conditionTest() {
    Condition condition = Utils.getTObject("condition/condition1.json", Condition.class);
    MybatisSqlVisitor sqlVisitor = new MybatisSqlVisitor();
    sqlVisitor.visit(condition);
    Assert.assertEquals("t0.`name` = #{_p1}", sqlVisitor.getSql());
    Assert.assertEquals("{_p1=luck}", sqlVisitor.getMybatisParamMap().toString());

    condition = Utils.getTObject("condition/condition2.json", Condition.class);
    sqlVisitor.reset();
    sqlVisitor.visit(condition);
    Assert.assertEquals("{_p2=2, _p1=1, _p4=4, _p3=3, _p5=5}",
        sqlVisitor.getMybatisParamMap().toString());
    Assert.assertEquals(
        "t0.`name` in (#{_p1},#{_p2},#{_p3},#{_p4},#{_p5})",
        sqlVisitor.getSql());

    condition = Utils.getTObject("condition/condition3.json", Condition.class);
    sqlVisitor.reset();
    sqlVisitor.visit(condition);
    Assert.assertEquals("{_p2=456, _p1=234}", sqlVisitor.getMybatisParamMap().toString());
    Assert.assertEquals("( t0.`id` = #{_p1} and t0.`name` <> #{_p2} )", sqlVisitor.getSql());
  }

  @Test
  public void betweenConditionTest() {
    Condition condition = condition = Utils.getTObject("condition/between1.json", Condition.class);
    MybatisSqlVisitor sqlVisitor = new MybatisSqlVisitor();
    sqlVisitor.visit(condition);
    Assert.assertEquals("{_p2=2020-01-01, _p1=2018-01-01}",
        sqlVisitor.getMybatisParamMap().toString());
    Assert.assertEquals("t0.`ts` between #{_p1} and #{_p2}", sqlVisitor.getSql());
  }

  @Test
  public void conditionsTest() {
    Conditions conditions = Utils.getTObject("condition/conditions1.json", Conditions.class);
    MybatisSqlVisitor sqlVisitor = new MybatisSqlVisitor();
    sqlVisitor.visit(conditions);
    Assert.assertEquals("where t0.`name` = #{_p1}", sqlVisitor.getSql());

    conditions = Utils.getTObject("condition/conditions2.json", Conditions.class);
    sqlVisitor.reset();
    sqlVisitor.visit(conditions);
    Assert.assertEquals("{_p2=2, _p1=1, _p4=4, _p3=3, _p5=5}",
        sqlVisitor.getMybatisParamMap().toString());
    Assert.assertEquals(
        "where t0.`name` in (#{_p1},#{_p2},#{_p3},#{_p4},#{_p5})",
        sqlVisitor.getSql());

    conditions = Utils.getTObject("condition/conditions3.json", Conditions.class);
    sqlVisitor.reset();
    sqlVisitor.visit(conditions);
    Assert.assertEquals("{_p2=456, _p1=234}", sqlVisitor.getMybatisParamMap().toString());
    Assert
        .assertEquals("where t0.`id` = #{_p1} and t0.`name` <> #{_p2}", sqlVisitor.getSql());

    conditions = Utils.getTObject("condition/conditions4.json", Conditions.class);
    sqlVisitor.reset();
    sqlVisitor.visit(conditions);
    Assert.assertEquals("{_p2=456, _p1=234}", sqlVisitor.getMybatisParamMap().toString());
    Assert
        .assertEquals("where ( t0.`id` = #{_p1} and t0.`name` <> #{_p2} )",
            sqlVisitor.getSql());
  }

}
