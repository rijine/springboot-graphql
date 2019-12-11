package metadata.mybatis;

import com.yonyou.einvoice.common.metadata.element.Conditions;
import com.yonyou.einvoice.common.metadata.visitor.MybatisSqlVisitor;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;

public class ConditionsTest {

  @Test
  public void equalTest() {
    Conditions conditions = Conditions.builder()
        .field("t0", "id").eq("123")
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(conditions);
    Assert.assertEquals("where t0.`id` = #{_p1}", sqlVisitor.getSql());
    Assert.assertEquals("{_p1=123}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test(expected = Exception.class)
  public void equalNullTest() {
    Conditions conditions = Conditions.builder()
        .field("t0", "id").eq(null)
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(conditions);
    Assert.assertEquals("where t0.`id` is null", sqlVisitor.getSql());
    Assert.assertEquals("{}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test(expected = Exception.class)
  public void and1Test() {
    Conditions conditions = Conditions.builder()
        .field("t0", "id").eq(null)
        .field("t0", "id").notEq(null)
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(conditions);
    Assert.assertEquals("where t0.`id` is null and t0.`id` is null", sqlVisitor.getSql());
    Assert.assertEquals("{}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test(expected = Exception.class)
  public void and2Test() {
    Conditions conditions = Conditions.builder()
        .andStart()
        .field("t0", "id").eq(null)
        .field("t0", "id").notEq(null)
        .andEnd()
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(conditions);
    Assert.assertEquals("where ( t0.`id` is null and t0.`id` is null )", sqlVisitor.getSql());
    Assert.assertEquals("{}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test(expected = Exception.class)
  public void and3Test() {
    Conditions conditions = Conditions.builder()
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
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(conditions);
    Assert.assertEquals(
        "where ( ( t0.`id` is null and t0.`id` is null ) and ( t0.`id` is null or t0.`id` is null ) )",
        sqlVisitor.getSql());
    Assert.assertEquals("{}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test
  public void inEmptyTest() {
    Conditions conditions = Conditions.builder()
        .field("t0", "id").in(null)
        .field("t0", "id").in(Collections.emptyList())
        .field("t0", "id").in(Arrays.asList("1", "2"))
        .field("t0", "id").notIn(null)
        .field("t0", "id").notIn(Collections.emptyList())
        .field("t0", "id").notIn(Arrays.asList("3", "4"))
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(conditions);
    Assert.assertEquals(
        "where 1 <> 1 and 1 <> 1 and t0.`id` in (#{_p1},#{_p2}) and 1 <> 1 and 1 <> 1 and t0.`id` not in (#{_p3},#{_p4})",
        sqlVisitor.getSql());
    Assert.assertEquals("{_p2=2, _p1=1, _p4=4, _p3=3}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test
  public void betweenTest() {
    Conditions conditions = Conditions.builder()
        .field("t0", "id").between("1", "2")
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(conditions);
    Assert.assertEquals("where t0.`id` between #{_p1} and #{_p2}", sqlVisitor.getSql());
    Assert.assertEquals("{_p2=2, _p1=1}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test(expected = Exception.class)
  public void betweenException1Test() {
    Conditions conditions = Conditions.builder()
        .field("t0", "id").between(null, "2")
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(conditions);
  }

  @Test(expected = Exception.class)
  public void betweenException2Test() {
    Conditions conditions = Conditions.builder()
        .field("t0", "id").between("1", null)
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(conditions);
  }

  @Test(expected = Exception.class)
  public void betweenException3Test() {
    Conditions conditions = Conditions.builder()
        .field("t0", "id").between(null, null)
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(conditions);
  }

  @Test(expected = Exception.class)
  public void likeEmptyTest() {
    Conditions conditions = Conditions.builder()
        .field("t0", "id").like(null)
        .field("t0", "id").like("1")
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(conditions);
    Assert.assertEquals(
        "where t0.`id` like #{_p1} and t0.`id` like #{_p2}",
        sqlVisitor.getSql());
    Assert.assertEquals("{_p2=%1%, _p1=%%}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test
  public void likeTest() {
    Conditions conditions = Conditions.builder()
        .field("t0", "id").like("1")
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(conditions);
    Assert.assertEquals("where t0.`id` like #{_p1}", sqlVisitor.getSql());
    Assert.assertEquals("{_p1=%1%}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test
  public void or1Test() {
    Conditions conditions = Conditions.builder()
        .orStart()
        .field("t0", "id").eq("t1", "hid")
        .orEnd()
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(conditions);
    Assert.assertEquals(
        "where ( t0.`id` = t1.`hid` )",
        sqlVisitor.getSql());
    Assert.assertEquals("{}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test
  public void or2Test() {
    Conditions conditions = Conditions.builder()
        .orStart()
        .field("t0", "id").eq("t1", "hid")
        .field("t0", "id").notEq("t1", "hid")
        .orEnd()
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(conditions);
    Assert.assertEquals(
        "where ( t0.`id` = t1.`hid` or t0.`id` <> t1.`hid` )",
        sqlVisitor.getSql());
    Assert.assertEquals("{}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test
  public void or3Test() {
    Conditions conditions = Conditions.builder()
        .orStart()
        .field("t0", "id").eq("t1", "hid")
        .field("t0", "id").notEq("t1", "hid")
        .orEnd()
        .field("t0", "id").in(Arrays.asList("1", "2", "3"))
        .build();
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(conditions);
    Assert.assertEquals(
        "where ( t0.`id` = t1.`hid` or t0.`id` <> t1.`hid` ) and t0.`id` in (#{_p1},#{_p2},#{_p3})",
        sqlVisitor.getSql());
    Assert.assertEquals("{_p2=2, _p1=1, _p3=3}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test
  public void or4Test() {
    Conditions conditions = Conditions.builder()
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
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(conditions);
    Assert.assertEquals(
        "where ( t0.`id` = t1.`hid` or t0.`id` <> t1.`hid` or ( t0.`id` = t1.`hid` and t0.`id` <> t1.`hid` ) ) and t0.`id` in (#{_p1},#{_p2},#{_p3})",
        sqlVisitor.getSql());
    Assert.assertEquals("{_p2=2, _p1=1, _p3=3}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test
  public void or5Test() {
    Conditions conditions = Conditions.builder()
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
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(conditions);
    Assert.assertEquals(
        "where ( t0.`id` = t1.`hid` or t0.`id` <> t1.`hid` or ( t0.`id` = t1.`hid` and t0.`id` <> t1.`hid` ) or ( t0.`id` = t1.`hid` and t0.`id` <> t1.`hid` ) ) and t0.`id` in (#{_p1},#{_p2},#{_p3})",
        sqlVisitor.getSql());
    Assert.assertEquals("{_p2=2, _p1=1, _p3=3}", sqlVisitor.getMybatisParamMap().toString());
  }

  @Test
  public void or6Test() {
    Conditions conditions = Conditions.builder()
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
    MybatisSqlVisitor sqlVisitor = getSqlVisitor(conditions);
    Assert.assertEquals(
        "where ( t0.`id` = t1.`hid` or t0.`id` <> t1.`hid` or ( t0.`id` = t1.`hid` and t0.`id` <> t1.`hid` ) or ( t0.`id` = t1.`hid` or t0.`id` <> t1.`hid` ) ) and t0.`id` in (#{_p1},#{_p2},#{_p3})",
        sqlVisitor.getSql());
    Assert.assertEquals("{_p2=2, _p1=1, _p3=3}", sqlVisitor.getMybatisParamMap().toString());
  }

  /**
   * 生成sqlVisitor，并执行sql、paramMap生成方法
   *
   * @param conditions
   * @return
   */
  private MybatisSqlVisitor getSqlVisitor(Conditions conditions) {
    MybatisSqlVisitor sqlVisitor = new MybatisSqlVisitor();
    sqlVisitor.visit(conditions);
    System.out.println(sqlVisitor.getSql());
    System.out.println(sqlVisitor.getMybatisParamMap());
    return sqlVisitor;
  }

}
