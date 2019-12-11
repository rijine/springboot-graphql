package metadata.base;

import com.yonyou.einvoice.common.metadata.element.Field;
import com.yonyou.einvoice.common.metadata.element.Fields;
import com.yonyou.einvoice.common.metadata.visitor.BaseSqlVisitor;
import metadata.Utils;
import org.junit.Assert;
import org.junit.Test;

public class FieldsTest {

  /**
   * 针对单个Field进行sql翻译判断
   */
  @Test
  public void fieldTest() {
    Field field = Utils.getTObject("field/field1.json", Field.class);
    BaseSqlVisitor sqlVisitor = new BaseSqlVisitor();
    sqlVisitor.visit(field);
    Assert.assertEquals("t0.`name`", sqlVisitor.getSql());

    field = Utils.getTObject("field/field2.json", Field.class);
    sqlVisitor.reset();
    sqlVisitor.visit(field);
    Assert.assertEquals("t0.`name` as n", sqlVisitor.getSql());
    field = Utils.getTObject("field/field3.json", Field.class);
    sqlVisitor.reset();
    sqlVisitor.visit(field);
    Assert.assertEquals("sum(t0.`name`) as n", sqlVisitor.getSql());

    field = Utils.getTObject("field/field4.json", Field.class);
    sqlVisitor.reset();
    sqlVisitor.visit(field);
    Assert.assertEquals("sum(distinct t0.`name`) as n", sqlVisitor.getSql());

    field = Utils.getTObject("field/field5.json", Field.class);
    sqlVisitor.reset();
    sqlVisitor.visit(field);
    Assert.assertEquals("count(distinct t0.`name`) as n", sqlVisitor.getSql());

    field = Utils.getTObject("field/field6.json", Field.class);
    sqlVisitor.reset();
    sqlVisitor.visit(field);
    Assert.assertEquals("count(t0.`name`) as n", sqlVisitor.getSql());

    field = Utils.getTObject("field/field7.json", Field.class);
    sqlVisitor.reset();
    sqlVisitor.visit(field);
    Assert.assertEquals("count(t0.`name`)", sqlVisitor.getSql());
  }

  /**
   * 针对fields进行整体翻译
   */
  @Test
  public void fieldsTest() {
    Fields fields = Utils.getTObject("field/fields1.json", Fields.class);
    BaseSqlVisitor sqlVisitor = new BaseSqlVisitor();
    sqlVisitor.reset();
    sqlVisitor.visit(fields);
    Assert.assertEquals("select t0.`name` as n, count(distinct t0.`id`) as i",
        sqlVisitor.getSql());

    fields = Utils.getTObject("field/fields2.json", Fields.class);
    sqlVisitor.reset();
    sqlVisitor.visit(fields);
    Assert.assertEquals("select distinct t0.`name` as n, count(distinct t0.`id`) as i",
        sqlVisitor.getSql());
  }

  @Test(expected = RuntimeException.class)
  public void fieldException() {
    Field field = Utils.getTObject("field/fieldexception.json", Field.class);
    BaseSqlVisitor sqlVisitor = new BaseSqlVisitor();
    sqlVisitor.visit(field);
  }
}
