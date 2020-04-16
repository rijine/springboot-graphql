package mp;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yonyou.einvoice.Application;
import com.yonyou.einvoice.common.agile.element.EntityCondition;
import com.yonyou.einvoice.common.agile.element.SqlOneEntity;
import com.yonyou.einvoice.common.agile.service.CommonEntityDao;
import com.yonyou.einvoice.extend.einvoicehis.entity.EinvoiceHisVO;
import com.yonyou.einvoice.extend.einvoicehis.repository.EinvoiceHisVOMapper;
import com.yonyou.einvoice.extend.einvoicehis.service.impl.EinvoiceHisVOServiceImpl;
import com.yonyou.einvoice.extend.einvoicehisb.entity.EinvoiceHisBVO;
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
public class ExtNoPermissionTest {

  @Autowired
  private EinvoiceHisVOServiceImpl einvoiceHisVOService;

  @Autowired
  private CommonEntityDao commonEntityDao;

  String fpqqlsh = "11223323";
  String tenantid = "test123";

  List<String> selectFields = Arrays.asList("id", "fpqqlsh");

  QueryWrapper<EinvoiceHisVO> queryWrapper = new QueryWrapper<EinvoiceHisVO>();

  {
    queryWrapper.eq("fpqqlsh", fpqqlsh).eq("tenantid", tenantid);
  }

  @Autowired
  private EinvoiceHisVOMapper einvoiceHisVOMapper;


  private static Long id;

  private static List<Long> idList = new ArrayList<>();


  @Test
  public void initDeleteAll() {
    einvoiceHisVOMapper.delete(null);
  }

  /**
   * 测试一下扩展表插入，包含批量插入
   */
  @Test
  public void test000InsertAndInsertBatch() {
    EinvoiceHisVO einvoiceHisExtVO = new EinvoiceHisVO();
    einvoiceHisExtVO.setKplx(1);
    einvoiceHisExtVO.setFpqqlsh(fpqqlsh);
    einvoiceHisExtVO.setTenantid(tenantid);
    einvoiceHisVOService.insert(einvoiceHisExtVO);
    id = einvoiceHisExtVO.getId();
    idList.add(einvoiceHisExtVO.getId());
    System.out.println(JSON.toJSONString(einvoiceHisExtVO));
    einvoiceHisVOService.insertBatchSomeColumn(Arrays.asList(einvoiceHisExtVO));
    idList.add(einvoiceHisExtVO.getId());
    System.out.println(JSON.toJSONString(einvoiceHisExtVO));
    Long tmpid = einvoiceHisExtVO.getId();
    checkEntityList(Arrays.asList(einvoiceHisExtVO), 1);
    einvoiceHisExtVO.setId(tmpid);
  }

  /**
   * 测试一下扩展表的查询
   */
  @Test
  public void test001SelectById() {
    EinvoiceHisVO einvoiceHisVO = einvoiceHisVOService.selectById(id);
    checkEntityObj(einvoiceHisVO);
    einvoiceHisVO.setId(id);
  }

  @Test
  public void test0020Select() {
    SqlOneEntity entity = SqlOneEntity.anBuilder(EinvoiceHisVO.class).build();
    System.out.println(entity.getSql());
    List<EinvoiceHisVO> einvoiceHisVOList = commonEntityDao
        .selectEntityOfType(entity, EinvoiceHisVO.class);
    System.out.println(einvoiceHisVOList);
  }

  @Test
  public void test002SelectBatchByIds() {
    List<EinvoiceHisVO> einvoiceHisVOList = einvoiceHisVOService.selectBatchIds(idList);
    checkEntityList(einvoiceHisVOList);
    einvoiceHisVOList.forEach(einvoiceHisVO -> {
      einvoiceHisVO.setId(id);
    });
  }

  @Test
  public void test003SelectByMap() {
    Map<String, Object> map = new TreeMap<>();
    map.put("fpqqlsh", "11223323");
    List<EinvoiceHisVO> einvoiceHisVOList = einvoiceHisVOService.selectByMap(map);
    checkEntityList(einvoiceHisVOList);
    einvoiceHisVOList.forEach(einvoiceHisVO -> {
      einvoiceHisVO.setId(id);
    });
  }

  @Test
  public void test004SelectCount() {
    Integer count = einvoiceHisVOService.selectCount(queryWrapper);
    Assert.assertNotNull(count);
    Assert.assertEquals(count.intValue(), 2);
  }

  @Test
  public void test00501SelectByDynamicMethod() {
    EntityCondition condition = EntityCondition.oneEntityConditionBuilder(EinvoiceHisVO.class)
        .where()
        .field(EinvoiceHisVO::getFpqqlsh).eq(fpqqlsh)
        .field(EinvoiceHisVO::getTenantid).eq(tenantid)
        .build();

    List<EinvoiceHisVO> einvoiceHisVOList = einvoiceHisVOService
        .selectByDynamicCondition(condition, EinvoiceHisVO::getId, EinvoiceHisVO::getGmfNsrsbh);
    checkEntityList(einvoiceHisVOList);
  }

  @Test
  public void test00502SelectByDynamicMethod() {
    EntityCondition condition = EntityCondition.oneEntityConditionBuilder(EinvoiceHisVO.class)
        .where()
        .field(EinvoiceHisVO::getFpqqlsh).eq(fpqqlsh)
        .field(EinvoiceHisVO::getTenantid).eq(tenantid)
        .build();

    List<EinvoiceHisVO> einvoiceHisVOList = einvoiceHisVOService
        .selectByDynamicCondition(condition, EinvoiceHisVO::getId);
    checkEntityList(einvoiceHisVOList);
  }

  @Test
  public void test00503SelectDynamic() {
    EntityCondition sourceCondition = EntityCondition
        .twoEntityConditionBuilder(EinvoiceHisVO.class, EinvoiceHisBVO.class)
        .innerJoin(EinvoiceHisBVO.class)
        .on(EinvoiceHisVO::getId, EinvoiceHisBVO::getHid)
        .where()
        .field(EinvoiceHisVO::getFpqqlsh).eq(fpqqlsh)
        .field(EinvoiceHisVO::getTenantid).eq(tenantid)
        .field(EinvoiceHisVO::getTenantid).eq(EinvoiceHisBVO::getCorpid)
        .orderbyAsc(EinvoiceHisVO::getId)
        .page(1, 15)
        .build();
    List<EinvoiceHisVO> einvoiceHisVOList = einvoiceHisVOService
        .selectByDynamicCondition(sourceCondition, EinvoiceHisVO::getId);
    Assert.assertNotNull(einvoiceHisVOList);
  }

  @Test
  public void test006SelectList() {
    List<EinvoiceHisVO> einvoiceHisVOList = einvoiceHisVOService.selectList(queryWrapper);
    checkEntityList(einvoiceHisVOList);
    einvoiceHisVOList.forEach(einvoiceHisVO -> {
      einvoiceHisVO.setId(id);
    });
  }

  @Test
  public void test007SelectMaps() {
    List<Map<String, Object>> result = einvoiceHisVOService.selectMaps(queryWrapper);
    Assert.assertNotNull(result);
    Assert.assertEquals(2, result.size());
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
  }

  @Test
  public void test011SelectPage() {
    IPage<EinvoiceHisVO> page = einvoiceHisVOService.selectPage(new Page<>(), queryWrapper);
    Assert.assertNotNull(page);
    Assert.assertNotNull(page.getRecords());
    page.getRecords().forEach(record -> {
      Assert.assertNotNull(record.getId());
      Assert.assertNotNull(record.getTs());
      record.setId(null);
      record.setTs(null);
    });
    System.out.println(JSON.toJSONString(page.getRecords()));
  }

  @Test
  public void test012SelectDynamic() {
    EntityCondition sourceCondition = EntityCondition.oneEntityConditionBuilder(EinvoiceHisVO.class)
        .where()
        .field(EinvoiceHisVO::getFpqqlsh).eq(fpqqlsh)
        .field(EinvoiceHisVO::getTenantid).eq(tenantid)
        .orderbyAsc(EinvoiceHisVO::getId)
        .page(1, 15)
        .build();
    List<EinvoiceHisVO> einvoiceHisVOList = einvoiceHisVOService
        .selectByDynamicCondition(sourceCondition, EinvoiceHisVO::getId);
    Assert.assertNotNull(einvoiceHisVOList);
    Assert.assertEquals(2, einvoiceHisVOList.size());
  }

  @Test
  public void test013SelectDynamic() {
    EntityCondition sourceCondition = EntityCondition.oneEntityConditionBuilder(EinvoiceHisVO.class)
        .where()
        .field(EinvoiceHisVO::getFpqqlsh).eq(fpqqlsh)
        .field(EinvoiceHisVO::getTenantid).eq(tenantid)
        .orderbyAsc(EinvoiceHisVO::getId)
        .page(1, 15)
        .build();
    List<EinvoiceHisVO> einvoiceHisVOList = einvoiceHisVOService
        .selectByDynamicCondition(sourceCondition, EinvoiceHisVO::getId);
    Assert.assertNotNull(einvoiceHisVOList);
    Assert.assertEquals(2, einvoiceHisVOList.size());
  }

  @Test
  public void test100Update() {
    QueryWrapper wrapper = new QueryWrapper();
    wrapper.eq("tenantid", tenantid);
    wrapper.eq("fpqqlsh", fpqqlsh);
    wrapper.eq("id", id);
    EinvoiceHisVO einvoiceHisVO = einvoiceHisVOService.selectById(id);
    Assert.assertNotNull(einvoiceHisVO);
    einvoiceHisVO.setFpqqlsh("432134");
    einvoiceHisVOService.update(einvoiceHisVO, wrapper);
    einvoiceHisVO = einvoiceHisVOService.selectById(id);
    Assert.assertEquals("432134", einvoiceHisVO.getFpqqlsh());
  }

  @Test
  public void test101UpdateById() {
    EinvoiceHisVO einvoiceHisVO = einvoiceHisVOService.selectById(id);
    Assert.assertNotNull(einvoiceHisVO);
    Assert.assertEquals("432134", einvoiceHisVO.getFpqqlsh());
    einvoiceHisVO.setFpqqlsh(fpqqlsh);
    einvoiceHisVOService.updateById(einvoiceHisVO);
    einvoiceHisVO = einvoiceHisVOService.selectById(id);
    Assert.assertEquals(fpqqlsh, einvoiceHisVO.getFpqqlsh());
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
    System.out.println(JSON.toJSONString(mapList));
    Assert.assertNotNull(mapList);
    Assert.assertEquals(2, mapList.size());
    for (Map<String, Object> map : mapList) {
      Assert.assertTrue(map.containsKey("id") || map.containsKey("ID"));
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
    }
    einvoiceHisVOList.forEach(einvoiceHisVO -> {
      einvoiceHisVO.setId(null);
    });
    System.out.println(JSON.toJSONString(einvoiceHisVOList));
  }


  private void checkEntityObj(EinvoiceHisVO einvoiceHisVO) {
    System.out.println(JSON.toJSONString(einvoiceHisVO));
    Assert.assertNotNull(einvoiceHisVO.getId());
    einvoiceHisVO.setId(null);
    System.out.println(JSON.toJSONString(einvoiceHisVO));
  }

  private void checkResult(String expected, EinvoiceHisVO obj) {
    System.out.println(JSON.toJSONString(obj));
    Assert.assertNotNull(obj.getId());
    obj.setId(null);
    obj.setTs(null);
    obj.setCreatetime(null);
    Assert.assertEquals(expected, JSON.toJSONString(obj));
  }

  private void checkResult(String expected, List<EinvoiceHisVO> objs) {
    System.out.println(JSON.toJSONString(objs));
    Assert.assertNotNull(objs);
    objs.forEach(obj -> {
      obj.setId(null);
      obj.setTs(null);
      obj.setCreatetime(null);
    });
    Assert.assertEquals(expected, JSON.toJSONString(objs));
  }
}
