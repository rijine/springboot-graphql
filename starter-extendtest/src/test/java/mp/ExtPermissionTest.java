package mp;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yonyou.einvoice.Application;
import com.yonyou.einvoice.common.agile.element.EntityCondition;
import com.yonyou.einvoice.common.agile.visitor.MybatisSqlVisitor;
import com.yonyou.einvoice.extend.einvoicehis.entity.EinvoiceHisVO;
import com.yonyou.einvoice.extend.einvoicehis.repository.EinvoiceHisVOMapper;
import com.yonyou.einvoice.extend.einvoicehis.service.impl.EinvoiceHisVOPermissionServiceImpl;
import com.yonyou.einvoice.extend.einvoicehisext.entity.EinvoiceHisExtVO;
import com.yonyou.einvoice.extend.einvoicehisext.repository.EinvoiceHisExtVOMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExtPermissionTest {

  @Autowired
  private EinvoiceHisVOPermissionServiceImpl einvoiceHisVOService;

  @Autowired
  private EinvoiceHisVOMapper einvoiceHisVOMapper;

  @Autowired
  private EinvoiceHisExtVOMapper einvoiceHisExtVOMapper;

  String fpqqlsh = "11223323";
  String tenantid = "test123";

  List<String> selectFields = Arrays.asList("id", "extId", "extTenantid", "fpqqlsh");

  QueryWrapper<EinvoiceHisVO> queryWrapper = new QueryWrapper<EinvoiceHisVO>();

  {
    queryWrapper.eq("fpqqlsh", fpqqlsh).eq("tenantid", tenantid);
  }

  private static Long id;

  private static List<Long> idList = new ArrayList<>();

  @Test
  public void initDeleteAll() {
    einvoiceHisVOMapper.delete(null);
    einvoiceHisExtVOMapper.delete(null);
  }

  /**
   * 测试一下扩展表插入，包含批量插入
   */
  @Test
  public void test000InsertAndInsertBatch() {
    EinvoiceHisExtVO einvoiceHisExtVO = new EinvoiceHisExtVO();
    einvoiceHisExtVO.setKplx(1);
    einvoiceHisExtVO.setFpqqlsh(fpqqlsh);
    einvoiceHisExtVO.setTenantid(tenantid);
    einvoiceHisExtVO.setExtTenantid(tenantid);
    einvoiceHisVOService.insert(einvoiceHisExtVO);
    id = einvoiceHisExtVO.getId();
    idList.add(einvoiceHisExtVO.getId());
    System.out.println(JSON.toJSONString(einvoiceHisExtVO));
    checkResult(
        "{\"extTenantid\":\"jowol828\",\"fpqqlsh\":\"11223323\",\"kplx\":1,\"tenantid\":\"jowol828\"}",
        einvoiceHisExtVO);
    einvoiceHisVOService.insertBatchSomeColumn(Arrays.asList(einvoiceHisExtVO));
    idList.add(einvoiceHisExtVO.getId());
    System.out.println(JSON.toJSONString(einvoiceHisExtVO));
    Long tmpid = einvoiceHisExtVO.getId();
    checkEntityList(Arrays.asList(einvoiceHisExtVO), 1);
    einvoiceHisExtVO.setId(tmpid);
    einvoiceHisExtVO.setExtId(tmpid);
    checkResult(
        "{\"extTenantid\":\"jowol828\",\"fpqqlsh\":\"11223323\",\"kplx\":1,\"tenantid\":\"jowol828\"}",
        einvoiceHisExtVO);
  }

  /**
   * 测试一下扩展表的查询
   */
  @Test
  public void test001SelectById() {
    EinvoiceHisVO einvoiceHisVO = einvoiceHisVOService.selectById(id);
    checkEntityObj(einvoiceHisVO);
    einvoiceHisVO.setId(id);
    ((EinvoiceHisExtVO) einvoiceHisVO).setExtId(id);
    Assert.assertTrue(einvoiceHisVO instanceof EinvoiceHisExtVO);
    checkResult(
        "{\"bmbBbh\":\"10.0\",\"creator\":\"~\",\"einvoiceHisBVOList\":[],\"extTenantid\":\"jowol828\",\"fpjz\":\"0\",\"fplx\":\"0\",\"fpqqlsh\":\"11223323\",\"kplx\":1,\"tenantid\":\"jowol828\",\"tschbz\":\"0\",\"zfbz\":\"N\",\"zsfs\":\"0\"}",
        einvoiceHisVO);
  }

  @Test
  public void test002SelectBatchByIds() {
    List<EinvoiceHisVO> einvoiceHisVOList = einvoiceHisVOService.selectBatchIds(idList);
    checkEntityList(einvoiceHisVOList);
    einvoiceHisVOList.forEach(einvoiceHisVO -> {
      einvoiceHisVO.setId(id);
      ((EinvoiceHisExtVO) einvoiceHisVO).setExtId(id);
    });
    checkResult(
        "[{\"bmbBbh\":\"10.0\",\"creator\":\"~\",\"einvoiceHisBVOList\":[],\"extTenantid\":\"jowol828\",\"fpjz\":\"0\",\"fplx\":\"0\",\"fpqqlsh\":\"11223323\",\"kplx\":1,\"tenantid\":\"jowol828\",\"tschbz\":\"0\",\"zfbz\":\"N\",\"zsfs\":\"0\"},{\"einvoiceHisBVOList\":[],\"extTenantid\":\"jowol828\",\"fpqqlsh\":\"11223323\",\"kplx\":1,\"tenantid\":\"jowol828\"}]",
        einvoiceHisVOList);
  }

  @Test
  public void test003SelectByMap() {
    Map<String, Object> map = new TreeMap<>();
    map.put("fpqqlsh", "11223323");
    List<EinvoiceHisVO> einvoiceHisVOList = einvoiceHisVOService.selectByMap(map);
    checkEntityList(einvoiceHisVOList);
    einvoiceHisVOList.forEach(einvoiceHisVO -> {
      einvoiceHisVO.setId(id);
      ((EinvoiceHisExtVO) einvoiceHisVO).setExtId(id);
    });
    checkResult(
        "[{\"bmbBbh\":\"10.0\",\"creator\":\"~\",\"einvoiceHisBVOList\":[],\"extTenantid\":\"jowol828\",\"fpjz\":\"0\",\"fplx\":\"0\",\"fpqqlsh\":\"11223323\",\"kplx\":1,\"tenantid\":\"jowol828\",\"tschbz\":\"0\",\"zfbz\":\"N\",\"zsfs\":\"0\"},{\"einvoiceHisBVOList\":[],\"extTenantid\":\"jowol828\",\"fpqqlsh\":\"11223323\",\"kplx\":1,\"tenantid\":\"jowol828\"}]",
        einvoiceHisVOList);
  }

  @Test
  public void test004SelectCount() {
    Integer count = einvoiceHisVOService.selectCount(queryWrapper);
    Assert.assertNotNull(count);
    Assert.assertEquals(count.intValue(), 0);
  }

  @Test
  public void test005SelectByDynamicMethod() {
    EntityCondition condition = EntityCondition.builder()
        .where()
        .field("t0", "fpqqlsh").eq(fpqqlsh)
        .field("t0", "tenantid").eq(tenantid)
        .build();
    List<EinvoiceHisVO> einvoiceHisVOList = einvoiceHisVOService
        .selectByDynamicCondition(condition, selectFields);
    checkEntityList(einvoiceHisVOList, 0);
    einvoiceHisVOList.forEach(einvoiceHisVO -> {
      einvoiceHisVO.setId(id);
      ((EinvoiceHisExtVO) einvoiceHisVO).setExtId(id);
    });
    checkResult("[]", einvoiceHisVOList);
  }

  @Test
  public void test006SelectList() {
    List<EinvoiceHisVO> einvoiceHisVOList = einvoiceHisVOService.selectList(queryWrapper);
    checkEntityList(einvoiceHisVOList, 0);
    einvoiceHisVOList.forEach(einvoiceHisVO -> {
      einvoiceHisVO.setId(id);
      ((EinvoiceHisExtVO) einvoiceHisVO).setExtId(id);
    });
    checkResult("[]", einvoiceHisVOList);
  }

  @Test
  public void test007SelectMaps() {
    List<Map<String, Object>> result = einvoiceHisVOService.selectMaps(queryWrapper);
    Assert.assertNotNull(result);
    Assert.assertEquals(0, result.size());
  }

  @Test
  public void test008SelectMapsPage() {
    IPage<Map<String, Object>> page = einvoiceHisVOService
        .selectMapsPage(new Page<>(), queryWrapper);
    Assert.assertNotNull(page);
    Assert.assertNotNull(page.getRecords());
    System.out.println(JSON.toJSONString(page.getRecords()));
  }

  @Test
  public void test009SelectObjs() {
    List<Object> objs = einvoiceHisVOService.selectObjs(queryWrapper);
    System.out.println(JSON.toJSONString(objs));
  }

  @Test
  public void test010SelectOne() {
    QueryWrapper<EinvoiceHisVO> wrapper = new QueryWrapper<>();
    wrapper.eq("fpqqlsh", "11223323");
    wrapper.eq("tenantid", "test123");
    wrapper.eq("id", id);
    EinvoiceHisVO einvoiceHisVO = einvoiceHisVOService.selectOne(wrapper);
    System.out.println(JSON.toJSONString(einvoiceHisVO));
    Assert.assertNull(einvoiceHisVO);
  }

  @Test
  public void test011SelectPage() {
    IPage<EinvoiceHisVO> page = einvoiceHisVOService.selectPage(new Page<>(), queryWrapper);
    Assert.assertNotNull(page);
    Assert.assertNotNull(page.getRecords());
    page.getRecords().forEach(record -> {
      Assert.assertTrue(record instanceof EinvoiceHisExtVO);
      Assert.assertNotNull(record.getId());
      Assert.assertNotNull(((EinvoiceHisExtVO) record).getExtId());
      Assert.assertNotNull(record.getTs());
      Assert.assertNotNull(((EinvoiceHisExtVO) record).getExtTs());
      record.setId(null);
      record.setTs(null);
      ((EinvoiceHisExtVO) record).setExtId(null);
      ((EinvoiceHisExtVO) record).setExtTs(null);
    });
    System.out.println(JSON.toJSONString(page.getRecords()));
  }

  @Test
  public void test012SelectDynamic() {
    EntityCondition sourceCondition = EntityCondition.builder()
        .where()
        .field("t0", "fpqqlsh").eq(fpqqlsh)
        .field("t0", "tenantid").eq(tenantid)
        .field("t0", "ext_tenantid").eq(tenantid)
        .orderbyAsc("t0", "id")
        .page(1, 15)
        .build();
    List<EinvoiceHisVO> einvoiceHisVOList = einvoiceHisVOService
        .selectByDynamicCondition(sourceCondition, selectFields);
    Assert.assertNotNull(einvoiceHisVOList);
    Assert.assertEquals(0, einvoiceHisVOList.size());
    checkEntityList(einvoiceHisVOList, 0);
  }

  @Test
  public void test013SelectDynamic() {
    EntityCondition sourceCondition = EntityCondition.builder()
        .where()
        .field("t0", "fpqqlsh").eq(fpqqlsh)
        .field("t0", "tenantid").eq(tenantid)
        .field("t0", "ext_tenantid").eq(tenantid)
        .orderbyAsc("t0", "id")
        .page(1, 15)
        .build();
    List<EinvoiceHisVO> einvoiceHisVOList = einvoiceHisVOService
        .selectByDynamicCondition(sourceCondition, Collections.emptyList());
    Assert.assertNotNull(einvoiceHisVOList);
    Assert.assertEquals(0, einvoiceHisVOList.size());
    checkEntityList(einvoiceHisVOList, 0);
  }

  /**
   * 主子查询。一条sql查出主、子数据，并进行拼接。 由于最早insert的数据无子表数据，所以此处查子表并不会有任何数据。
   */
  @Test
  public void test014SelectWithRelationByDynamicCondition() {
    EntityCondition sourceCondition = EntityCondition.builder()
        .innerJoin("einvoice_his_b", "t1").on("t0", "id", "t1", "hid")
        .where()
        .field("t0", "fpqqlsh").eq(fpqqlsh)
        .field("t0", "tenantid").eq(tenantid)
        .field("t0", "ext_tenantid").eq(tenantid)
        .build();
    MybatisSqlVisitor visitor = new MybatisSqlVisitor();
    visitor.visit(sourceCondition);
    Assert.assertEquals(
        "inner join einvoice_his_b t1 on t0.`id` = t1.`hid` where t0.`fpqqlsh` = #{_p1} and t0.`tenantid` = #{_p2} and t0.`ext_tenantid` = #{_p3}",
        visitor.getSql());
    List<EinvoiceHisVO> einvoiceHisVOList = einvoiceHisVOService
        .selectWithRelationByDynamicCondition(sourceCondition);
    Assert.assertNotNull(einvoiceHisVOList);
    Assert.assertEquals(0, einvoiceHisVOList.size());
    checkEntityList(einvoiceHisVOList, 0);
  }

  @Test
  public void test100Update() {
    QueryWrapper wrapper = new QueryWrapper();
    wrapper.eq("tenantid", tenantid);
    wrapper.eq("fpqqlsh", fpqqlsh);
    wrapper.eq("id", id);
    EinvoiceHisVO einvoiceHisVO = einvoiceHisVOService.selectById(id);
    Assert.assertNotNull(einvoiceHisVO);
    Assert.assertTrue(einvoiceHisVO instanceof EinvoiceHisExtVO);
    einvoiceHisVO.setFpqqlsh("432134");
    ((EinvoiceHisExtVO) einvoiceHisVO).setExtTenantid("543443");
    einvoiceHisVOService.update(einvoiceHisVO, wrapper);
    einvoiceHisVO = einvoiceHisVOService.selectById(id);
    Assert.assertEquals(fpqqlsh, einvoiceHisVO.getFpqqlsh());
    Assert.assertNotEquals(tenantid, ((EinvoiceHisExtVO) einvoiceHisVO).getExtTenantid());
  }

  @Test
  public void test101UpdateById() {
    EinvoiceHisVO einvoiceHisVO = einvoiceHisVOService.selectById(id);
    Assert.assertNotNull(einvoiceHisVO);
    Assert.assertTrue(einvoiceHisVO instanceof EinvoiceHisExtVO);
    Assert.assertNotEquals("432134", einvoiceHisVO.getFpqqlsh());
    Assert.assertNotEquals(tenantid, ((EinvoiceHisExtVO) einvoiceHisVO).getExtTenantid());
    einvoiceHisVO.setFpqqlsh(fpqqlsh);
    ((EinvoiceHisExtVO) einvoiceHisVO).setExtTenantid(tenantid);
    einvoiceHisVOService.updateById(einvoiceHisVO);
    einvoiceHisVO = einvoiceHisVOService.selectById(id);
    Assert.assertEquals(null, einvoiceHisVO);
  }

  @Test(expected = Exception.class)
  public void test200Delete() {
    einvoiceHisVOService.delete(null);
  }

  @Test(expected = Exception.class)
  public void test201DeleteById() {
    einvoiceHisVOService.deleteById(null);
  }

  @Test(expected = Exception.class)
  public void test202DeleteByMap() {
    einvoiceHisVOService.deleteByMap(null);
  }

  @Test(expected = Exception.class)
  public void test203DeleteByMap() {
    einvoiceHisVOService.deleteByMap(Collections.emptyMap());
  }

  @Test(expected = Exception.class)
  public void test204DeleteByBatchIds() {
    einvoiceHisVOService.deleteBatchIds(null);
  }

  @Test(expected = Exception.class)
  public void test205DeleteByBatchIds() {
    einvoiceHisVOService.deleteBatchIds(Collections.emptyList());
  }

  @Test(expected = Exception.class)
  public void test206Delete() {
    einvoiceHisVOService.delete(new QueryWrapper<>());
  }

  @Test
  public void test207Delete() {
    QueryWrapper queryWrapper = new QueryWrapper();
    // 示例中数据库主键自增，id基本不会增长到10000000。因此，使用该条件可以清空所有数据。
    queryWrapper.ne("id", 10000000);
    einvoiceHisVOService.delete(queryWrapper);
  }

  private void checkMapList(List<Map<String, Object>> mapList) {
    checkMapList(mapList, 2);
  }

  private void checkMapList(List<Map<String, Object>> mapList, int num) {
    System.out.println(JSON.toJSONString(mapList));
    Assert.assertNotNull(mapList);
    Assert.assertEquals(num, mapList.size());
    for (Map<String, Object> map : mapList) {
      Assert.assertTrue(map.containsKey("id"));
      Assert.assertTrue(map.containsKey("ext_id"));
      Assert.assertEquals(map.get("id"), map.get("ext_id"));
      Assert.assertNotNull(map.get("ext_tenantid"));
    }
  }

  private void checkEntityList(List<EinvoiceHisVO> einvoiceHisVOList) {
    checkEntityList(einvoiceHisVOList, 2);
  }

  private void checkEntityList(List<EinvoiceHisVO> einvoiceHisVOList, int count) {
    System.out.println(JSON.toJSONString(einvoiceHisVOList));
    Assert.assertNotNull(einvoiceHisVOList);
    Assert.assertEquals(count, einvoiceHisVOList.size());
    for (EinvoiceHisVO einvoiceHisVO : einvoiceHisVOList) {
      Assert.assertNotNull(einvoiceHisVO.getId());
      Assert.assertNotNull(((EinvoiceHisExtVO) einvoiceHisVO).getExtId());
      Assert.assertEquals(einvoiceHisVO.getId(), ((EinvoiceHisExtVO) einvoiceHisVO).getExtId());
    }
    einvoiceHisVOList.forEach(einvoiceHisVO -> {
      einvoiceHisVO.setId(null);
      ((EinvoiceHisExtVO) einvoiceHisVO).setExtId(null);
    });
    System.out.println(JSON.toJSONString(einvoiceHisVOList));
  }


  private void checkEntityObj(EinvoiceHisVO einvoiceHisVO) {
    System.out.println(JSON.toJSONString(einvoiceHisVO));
    Assert.assertTrue(einvoiceHisVO instanceof EinvoiceHisExtVO);
    Assert.assertNotNull(einvoiceHisVO.getId());
    Assert.assertNotNull(((EinvoiceHisExtVO) einvoiceHisVO).getExtId());
    Assert.assertEquals(einvoiceHisVO.getId(), ((EinvoiceHisExtVO) einvoiceHisVO).getExtId());
    einvoiceHisVO.setId(null);
    ((EinvoiceHisExtVO) einvoiceHisVO).setExtId(null);
    System.out.println(JSON.toJSONString(einvoiceHisVO));
  }

  private void checkResult(String expected, EinvoiceHisVO obj) {
    System.out.println(JSON.toJSONString(obj));
    Assert.assertTrue(obj instanceof EinvoiceHisExtVO);
    Assert.assertNotNull(obj.getId());
    Assert.assertNotNull(((EinvoiceHisExtVO) obj).getExtId());
    obj.setId(null);
    ((EinvoiceHisExtVO) obj).setExtId(null);
    obj.setTs(null);
    obj.setCreatetime(null);
    ((EinvoiceHisExtVO) obj).setExtTs(null);
    Assert.assertEquals(expected, JSON.toJSONString(obj));
  }

  private void checkResult(String expected, List<EinvoiceHisVO> objs) {
    System.out.println(JSON.toJSONString(objs));
    Assert.assertNotNull(objs);
    objs.forEach(obj -> {
      obj.setId(null);
      obj.setTs(null);
      obj.setCreatetime(null);
      ((EinvoiceHisExtVO) obj).setExtId(null);
      ((EinvoiceHisExtVO) obj).setExtTs(null);
      Assert.assertTrue(obj instanceof EinvoiceHisExtVO);
    });
    Assert.assertEquals(expected, JSON.toJSONString(objs));
  }
}
