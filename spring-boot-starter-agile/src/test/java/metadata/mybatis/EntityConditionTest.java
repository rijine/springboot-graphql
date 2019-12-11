package metadata.mybatis;

import com.yonyou.einvoice.common.agile.element.EntityCondition;
import com.yonyou.einvoice.common.agile.enums.OperatorEnum;
import com.yonyou.einvoice.common.agile.visitor.MybatisSqlVisitor;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;

public class EntityConditionTest {

  @Test
  public void equalTest() {
    EntityCondition sourceCondition = EntityCondition.builder()
        .where()
        .field("t0", "id").eq("123")
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(sourceCondition);
    Assert.assertEquals("where t0.`id` = #{_p1}", sqlVisitor.getSql());
    Assert.assertEquals("{_p1=123}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test(expected = Exception.class)
  public void equalNullTest() {
    EntityCondition sourceCondition = EntityCondition.builder()
        .where()
        .field("t0", "id").eq(null)
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(sourceCondition);
    Assert.assertEquals("where t0.`id` is null", sqlVisitor.getSql());
    Assert.assertEquals("{}", sqlVisitor.getMybatisParamMap().toString());
  }


  @Test(expected = Exception.class)
  public void and1Test() {
    EntityCondition sourceCondition = EntityCondition.builder()
        .where()
        .field("t0", "id").eq(null)
        .field("t0", "id").eq(null)
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(sourceCondition);
    Assert.assertEquals("where t0.`id` is null and t0.`id` is null", sqlVisitor.getSql());
    Assert.assertEquals("{}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test(expected = Exception.class)
  public void and2Test() {
    EntityCondition sourceCondition = EntityCondition.builder()
        .where()
        .andStart()
        .field("t0", "id").eq(null)
        .field("t0", "id").eq(null)
        .andEnd()
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(sourceCondition);
    Assert.assertEquals("where ( t0.`id` is null and t0.`id` is null )", sqlVisitor.getSql());
    Assert.assertEquals("{}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test(expected = Exception.class)
  public void and3Test() {
    EntityCondition sourceCondition = EntityCondition.builder()
        .where()
        .andStart()
        .andStart()
        .field("t0", "id").eq(null)
        .field("t0", "id").notEq(null)
        .andEnd()
        .orStart()
        .field("t0", "id").eq(null)
        .field("t0", "id").notEq(null)
        .orEnd()
        .andEnd()
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(sourceCondition);
    Assert.assertEquals(
        "where ( ( t0.`id` is null and t0.`id` is null ) and ( t0.`id` is null or t0.`id` is null ) )",
        sqlVisitor.getSql());
    Assert.assertEquals("{}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test
  public void inEmptyTest() {
    EntityCondition sourceCondition = EntityCondition.builder()
        .where()
        .field("t0", "id").in(null)
        .field("t0", "id").in(Collections.emptyList())
        .field("t0", "id").in(Arrays.asList("1", "2"))
        .field("t0", "id").notIn(null)
        .field("t0", "id").notIn(Collections.emptyList())
        .field("t0", "id").notIn(Arrays.asList("3", "4"))
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(sourceCondition);
    Assert.assertEquals(
        "where 1 <> 1 and 1 <> 1 and t0.`id` in (#{_p1},#{_p2}) and 1 <> 1 and 1 <> 1 and t0.`id` not in (#{_p3},#{_p4})",
        sqlVisitor.getSql());
    Assert.assertEquals("{_p2=2, _p1=1, _p4=4, _p3=3}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test
  public void betweenTest() {
    EntityCondition sourceCondition = EntityCondition.builder()
        .where()
        .field("t0", "id").between("1", "2")
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(sourceCondition);
    Assert.assertEquals("where t0.`id` between #{_p1} and #{_p2}", sqlVisitor.getSql());
    Assert.assertEquals("{_p2=2, _p1=1}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test(expected = Exception.class)
  public void betweenException1Test() {
    EntityCondition sourceCondition = EntityCondition.builder()
        .where()
        .field("t0", "id").between(null, "2")
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(sourceCondition);
  }

  @Test(expected = Exception.class)
  public void betweenException2Test() {
    EntityCondition sourceCondition = EntityCondition.builder()
        .where()
        .field("t0", "id").between("1", null)
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(sourceCondition);
  }

  @Test(expected = Exception.class)
  public void betweenException3Test() {
    EntityCondition sourceCondition = EntityCondition.builder()
        .where()
        .field("t0", "id").between(null, null)
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(sourceCondition);
  }

  @Test
  public void joinTest1() {
    EntityCondition sourceCondition = EntityCondition.builder()
        .innerJoin("einvoice_his_b", "t1").on("t0", "id", "t1", "hid")
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(sourceCondition);
    Assert.assertEquals("inner join einvoice_his_b t1 on t0.`id` = t1.`hid`", sqlVisitor.getSql());
    Assert.assertEquals("{}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test
  public void joinTest2() {
    EntityCondition sourceCondition = EntityCondition.builder()
        .innerJoin("einvoice_his_b", "t1").on("t0", "id", "t1", "hid")
        .innerJoin("organization", "t2").on("t0", "orgid", "t2", "id")
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(sourceCondition);
    Assert.assertEquals(
        "inner join einvoice_his_b t1 on t0.`id` = t1.`hid` inner join organization t2 on t0.`orgid` = t2.`id`",
        sqlVisitor.getSql());
    Assert.assertEquals("{}", sqlVisitor.getMybatisParamMap().toString());
  }


  @Test
  public void whereJoinTest() {
    EntityCondition sourceCondition = EntityCondition.builder()
        .innerJoin("einvoice_his_b", "t1").on("t0", "id", "t1", "hid")
        .innerJoin("organization", "t2").on("t0", "orgid", "t2", "id")
        .where()
        .field("t0", "id").eq("t1", "hid")
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(sourceCondition);
    Assert.assertEquals(
        "inner join einvoice_his_b t1 on t0.`id` = t1.`hid` inner join organization t2 on t0.`orgid` = t2.`id` where t0.`id` = t1.`hid`",
        sqlVisitor.getSql());
    Assert.assertEquals("{}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test
  public void or1Test() {
    EntityCondition sourceCondition = EntityCondition.builder()
        .innerJoin("einvoice_his_b", "t1").on("t0", "id", "t1", "hid")
        .innerJoin("organization", "t2").on("t0", "orgid", "t2", "id")
        .where()
        .orStart()
        .field("t0", "id").eq("t1", "hid")
        .orEnd()
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(sourceCondition);
    Assert.assertEquals(
        "inner join einvoice_his_b t1 on t0.`id` = t1.`hid` inner join organization t2 on t0.`orgid` = t2.`id` where ( t0.`id` = t1.`hid` )",
        sqlVisitor.getSql());
    Assert.assertEquals("{}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test
  public void or2Test() {
    EntityCondition sourceCondition = EntityCondition.builder()
        .innerJoin("einvoice_his_b", "t1").on("t0", "id", "t1", "hid")
        .innerJoin("organization", "t2").on("t0", "orgid", "t2", "id")
        .where()
        .orStart()
        .field("t0", "id").eq("t1", "hid")
        .field("t0", "id").notEq("t1", "hid")
        .orEnd()
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(sourceCondition);
    Assert.assertEquals(
        "inner join einvoice_his_b t1 on t0.`id` = t1.`hid` inner join organization t2 on t0.`orgid` = t2.`id` where ( t0.`id` = t1.`hid` or t0.`id` <> t1.`hid` )",
        sqlVisitor.getSql());
    Assert.assertEquals("{}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test
  public void or3Test() {
    EntityCondition sourceCondition = EntityCondition.builder()
        .innerJoin("einvoice_his_b", "t1").on("t0", "id", "t1", "hid")
        .innerJoin("organization", "t2").on("t0", "orgid", "t2", "id")
        .where()
        .orStart()
        .field("t0", "id").eq("t1", "hid")
        .field("t0", "id").notEq("t1", "hid")
        .orEnd()
        .field("t0", "id").in(Arrays.asList("1", "2", "3"))
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(sourceCondition);
    Assert.assertEquals(
        "inner join einvoice_his_b t1 on t0.`id` = t1.`hid` inner join organization t2 on t0.`orgid` = t2.`id` where ( t0.`id` = t1.`hid` or t0.`id` <> t1.`hid` ) and t0.`id` in (#{_p1},#{_p2},#{_p3})",
        sqlVisitor.getSql());
    Assert.assertEquals("{_p2=2, _p1=1, _p3=3}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test
  public void or4Test() {
    EntityCondition sourceCondition = EntityCondition.builder()
        .innerJoin("einvoice_his_b", "t1").on("t0", "id", "t1", "hid")
        .innerJoin("organization", "t2").on("t0", "orgid", "t2", "id")
        .where()
        .orStart()
        .field("t0", "id").eq("t1", "hid")
        .field("t0", "id").notEq("t1", "hid")
        .andStart()
        .field("t0", "id").eq("t1", "hid")
        .field("t0", "id").notEq("t1", "hid")
        .andEnd()
        .orEnd()
        .field("t0", "id").in(Arrays.asList("1", "2", "3"))
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(sourceCondition);
    Assert.assertEquals(
        "inner join einvoice_his_b t1 on t0.`id` = t1.`hid` inner join organization t2 on t0.`orgid` = t2.`id` where ( t0.`id` = t1.`hid` or t0.`id` <> t1.`hid` or ( t0.`id` = t1.`hid` and t0.`id` <> t1.`hid` ) ) and t0.`id` in (#{_p1},#{_p2},#{_p3})",
        sqlVisitor.getSql());
    Assert.assertEquals("{_p2=2, _p1=1, _p3=3}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test
  public void or5Test() {
    EntityCondition sourceCondition = EntityCondition.builder()
        .innerJoin("einvoice_his_b", "t1").on("t0", "id", "t1", "hid")
        .innerJoin("organization", "t2").on("t0", "orgid", "t2", "id")
        .where()
        .orStart()
        .field("t0", "id").eq("t1", "hid")
        .field("t0", "id").notEq("t1", "hid")
        .andStart()
        .field("t0", "id").eq("t1", "hid")
        .field("t0", "id").notEq("t1", "hid")
        .andEnd()
        .andStart()
        .field("t0", "id").eq("t1", "hid")
        .field("t0", "id").notEq("t1", "hid")
        .andEnd()
        .orEnd()
        .field("t0", "id").in(Arrays.asList("1", "2", "3"))
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(sourceCondition);
    Assert.assertEquals(
        "inner join einvoice_his_b t1 on t0.`id` = t1.`hid` inner join organization t2 on t0.`orgid` = t2.`id` where ( t0.`id` = t1.`hid` or t0.`id` <> t1.`hid` or ( t0.`id` = t1.`hid` and t0.`id` <> t1.`hid` ) or ( t0.`id` = t1.`hid` and t0.`id` <> t1.`hid` ) ) and t0.`id` in (#{_p1},#{_p2},#{_p3})",
        sqlVisitor.getSql());
    Assert.assertEquals("{_p2=2, _p1=1, _p3=3}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test
  public void or6Test() {
    EntityCondition sourceCondition = EntityCondition.builder()
        .innerJoin("einvoice_his_b", "t1").on("t0", "id", "t1", "hid")
        .innerJoin("organization", "t2").on("t0", "orgid", "t2", "id")
        .where()
        .orStart()
        .field("t0", "id").eq("t1", "hid")
        .field("t0", "id").notEq("t1", "hid")
        .andStart()
        .field("t0", "id").eq("t1", "hid")
        .field("t0", "id").notEq("t1", "hid")
        .andEnd()
        .orStart()
        .field("t0", "id").eq("t1", "hid")
        .field("t0", "id").notEq("t1", "hid")
        .orEnd()
        .orEnd()
        .field("t0", "id").in(Arrays.asList("1", "2", "3"))
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(sourceCondition);
    Assert.assertEquals(
        "inner join einvoice_his_b t1 on t0.`id` = t1.`hid` inner join organization t2 on t0.`orgid` = t2.`id` where ( t0.`id` = t1.`hid` or t0.`id` <> t1.`hid` or ( t0.`id` = t1.`hid` and t0.`id` <> t1.`hid` ) or ( t0.`id` = t1.`hid` or t0.`id` <> t1.`hid` ) ) and t0.`id` in (#{_p1},#{_p2},#{_p3})",
        sqlVisitor.getSql());
    Assert.assertEquals("{_p2=2, _p1=1, _p3=3}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test
  public void testAll() {
    EntityCondition sourceCondition = EntityCondition.builder()
        .innerJoin("einvoice_his_b", "t1").on("t0", "id", "t1", "hid")
        .innerJoin("organization", "t2").on("t0", "orgid", "t2", "id")
        .where()
        .field("t0", "id").eq("1")
        .field("t0", "id").likeEnd("1")
        .field("t0", "id").eq("t1", "hid")
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(sourceCondition);
    Assert.assertEquals(
        "inner join einvoice_his_b t1 on t0.`id` = t1.`hid` inner join organization t2 on t0.`orgid` = t2.`id` where t0.`id` = #{_p1} and t0.`id` like #{_p2} and t0.`id` = t1.`hid`",
        sqlVisitor.getSql());
    Assert.assertEquals("{_p2=%1, _p1=1}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test
  public void having1Test() {
    EntityCondition sourceCondition = EntityCondition.builder()
        .where()
        .havingField("t0", "id")
        .havingOperator(OperatorEnum.GREATER)
        .havingStrValue("123")
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(sourceCondition);
    Assert.assertEquals("having t0.`id` > #{_p1}", sqlVisitor.getSql());
    Assert.assertEquals("{_p1=123}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test
  public void having2Test() {
    EntityCondition sourceCondition = EntityCondition.builder()
        .where()
        .havingField("t0", "id").havingOperator(OperatorEnum.GREATER).havingStrValue("123")
        .havingField("t0", "id").havingOperator(OperatorEnum.LESS).havingStrValue("456")
        .havingField("t0", "id").havingOperator(OperatorEnum.LIKE).havingStrValue("%45%")
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(sourceCondition);
    Assert.assertEquals("having t0.`id` > #{_p1} and t0.`id` < #{_p2} and t0.`id` like #{_p3}",
        sqlVisitor.getSql());
    Assert.assertEquals("{_p2=456, _p1=123, _p3=%45%}", sqlVisitor.getMybatisParamMap().toString());
  }

  /**
   * 生成sqlVisitor，并执行sql、paramMap生成方法
   *
   * @param sourceCondition
   * @return
   */
  private MybatisSqlVisitor getSqlVisitor(EntityCondition sourceCondition) {
    MybatisSqlVisitor sqlVisitor = new MybatisSqlVisitor();
    sqlVisitor.visit(sourceCondition);
    System.out.println(sqlVisitor.getSql());
    System.out.println(sqlVisitor.getMybatisParamMap());
    return sqlVisitor;
  }

}
