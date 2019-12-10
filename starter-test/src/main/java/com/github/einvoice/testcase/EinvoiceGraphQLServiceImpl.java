package com.github.einvoice.testcase;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.einvoice.testcase.einvoicehis.entity.EinvoiceHisVO;
import com.github.einvoice.testcase.einvoicehis.service.IEinvoiceHisVOService;
import com.github.einvoice.testcase.einvoicehisb.entity.EinvoiceHisBVO;
import com.github.einvoice.testcase.einvoicehisb.service.IEinvoiceHisBVOService;
import com.yonyou.einvoice.common.metadata.element.EntityCondition;
import com.yonyou.einvoice.common.metadata.graphql.IGraphQLService;
import graphql.execution.batched.Batched;
import graphql.language.Field;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class EinvoiceGraphQLServiceImpl implements IGraphQLService {

  /**
   * 这个service用于测试不自动添加权限控制的service
   */
  @Autowired
  @Qualifier(value = "einvoiceHisVOServiceImpl")
  private IEinvoiceHisVOService einvoiceHisVOService;

  /**
   * 这个service用于测试自动添加权限控制的service
   */
  @Autowired
  @Qualifier(value = "einvoiceHisVOPermissionServiceImpl")
  private IEinvoiceHisVOService einvoiceHisVOPermissionService;

  /**
   * 用于完成主子查询的子service
   */
  @Autowired
  @Qualifier(value = "einvoiceHisBVOServiceImpl")
  private IEinvoiceHisBVOService einvoiceHisBVOService;

  /**
   * 用于完成主子查询的子service
   */
  @Autowired
  @Qualifier(value = "einvoiceHisBVOPermissionServiceImpl")
  private IEinvoiceHisBVOService einvoiceHisBVOPermissionService;

  /**
   * 用于测试运行时动态拼接查询条件的service，不自动添加权限控制。（limit分页）
   *
   * @param dynamicCond
   * @param field
   * @return
   */
  @GraphQLQuery(name = "einvoiceHisDynamic")
  public List<EinvoiceHisVO> getEinvoiceHisVODynamic(
      @GraphQLArgument(name = "dynamicCond") EntityCondition dynamicCond, @GraphQLEnvironment
      Field field) {
    List<EinvoiceHisVO> einvoiceHisVOList = einvoiceHisVOService
        .selectByDynamicCondition(dynamicCond, field);
    QueryWrapper queryWrapper = new QueryWrapper();
    return einvoiceHisVOList;
  }

  /**
   * 与上相同，用于测试分页（不添加权限控制）。获取分页总数
   *
   * @param dynamicCond
   * @param field
   * @return
   */
  @GraphQLQuery(name = "einvoiceHisCountAllDynamic")
  public Integer getEinvoiceHisCountAllDynamic(
      @GraphQLArgument(name = "dynamicCond") EntityCondition dynamicCond,
      @GraphQLEnvironment Field field) {
    Integer count = einvoiceHisVOService.countAllByDynamicCondition(dynamicCond);
    return count;
  }

  /**
   * 测试主子查询。
   *
   * @param hisVOList
   * @param field
   * @return
   * @Batched，用于批量根据主对象列表查询子对象列表列表（一对多的主子关系）
   * @GraphQLArgument是graphql生成schema时的参数，必填。
   * @GraphQLContext用于注明，该方法的主对象是什么类型。@Batched与@GraphQLContext结合使用，则主对象类型要为List类型。（参数与返回值list大小一致）
   * @GraphQLEnvironment。用于获取本次查询所需的字段。在执行select语句时，可以仅select所查询的字段（提升数据库性能）
   */
  @Batched
  @GraphQLQuery(name = "bvoList")
  public List<List<EinvoiceHisBVO>> getBvoList(
      @GraphQLArgument(name = "hisList") @GraphQLContext List<EinvoiceHisVO> hisVOList,
      @GraphQLEnvironment Field field) {
    List<String> selectFields = einvoiceHisBVOService.getSelectFields(field);
    QueryWrapper<EinvoiceHisBVO> queryWrapper = new QueryWrapper<>();
    queryWrapper.select(selectFields.toArray(new String[selectFields.size()]));
    queryWrapper
        .in("hid", hisVOList.stream().map(EinvoiceHisVO::getId).collect(Collectors.toList()));
    List<EinvoiceHisBVO> einvoiceHisBVOList = einvoiceHisBVOService.selectList(queryWrapper);
    List<List<EinvoiceHisBVO>> resultList = einvoiceHisBVOService
        .getSubFieldListOfList(einvoiceHisBVOList, EinvoiceHisBVO::getHid,
            hisVOList.stream().map(EinvoiceHisVO::getId));
    return resultList;
  }

  /**
   * 自动添加权限控制的查询（limit分页）
   *
   * @param dynamicCond
   * @param field
   * @return
   */
  @GraphQLQuery(name = "einvoiceHisPermissionDynamic")
  public List<EinvoiceHisVO> getEinvoiceHisPermissionVODynamic(
      @GraphQLArgument(name = "dynamicCond") EntityCondition dynamicCond,
      @GraphQLEnvironment Field field) {
    List<EinvoiceHisVO> einvoiceHisVOList = einvoiceHisVOPermissionService
        .selectByDynamicCondition(dynamicCond, field);
    return einvoiceHisVOList;
  }

  /**
   * 自动添加权限控制的查询，获取符合查询条件的总数
   *
   * @param dynamicCond
   * @return
   */
  @GraphQLQuery(name = "einvoiceHisPermissionCountDynamic")
  public Integer getEinvoiceHisPermissionCountDynamic(
      @GraphQLArgument(name = "dynamicCond") EntityCondition dynamicCond) {
    Integer count = einvoiceHisVOPermissionService.countAllByDynamicCondition(dynamicCond);
    return count;
  }

  /**
   * 用于获取1：1关联查询。 由于主-关联关系为1：1，因此参数列表与返回值列表均为List，且List大小一致。
   *
   * @param hisVOList
   * @return
   */
  @Batched
  @GraphQLQuery(name = "artificialName")
  public List<String> getArtificialName(
      @GraphQLArgument(name = "hisList") @GraphQLContext List<EinvoiceHisVO> hisVOList) {
    List<String> resultList = hisVOList.stream()
        .map(einvoiceHisVO -> einvoiceHisVO.getId() + "-" + einvoiceHisVO.getFpqqlsh()).collect(
            Collectors.toList());
    return resultList;
  }

  /**
   * 用于测试子查询中产生异常
   *
   * @param hisVOList
   * @return
   */
  @Batched
  @GraphQLQuery(name = "artificialNameException")
  public List<String> getArtificialNameException(
      @GraphQLArgument(name = "hisList") @GraphQLContext List<EinvoiceHisVO> hisVOList) {
    List<String> resultList = hisVOList.stream()
        .map(einvoiceHisVO -> einvoiceHisVO.getId() + "-" + einvoiceHisVO.getFpqqlsh()).collect(
            Collectors.toList());
    throw new RuntimeException("测试子查询异常抛出");
  }

  /**
   * 用于测试单独查询中产生异常
   *
   * @return
   */
  @GraphQLQuery(name = "testException")
  public List<EinvoiceHisVO> testException() {
    throw new RuntimeException("测试异常抛出");
  }

  /**
   * 测试不自动添加权限控制的批量插入
   *
   * @param einvoiceHisVOList
   * @return
   */
  @GraphQLMutation(name = "saveEinvoiceHisList")
  public String einvoiceHisInsert(
      @GraphQLArgument(name = "einvoiceHisList") List<EinvoiceHisVO> einvoiceHisVOList) {
    einvoiceHisVOService.insertBatchSomeColumn(einvoiceHisVOList);
    List<EinvoiceHisBVO> einvoiceHisBVOList = getEinvoiceHisBVOList(einvoiceHisVOList);
    einvoiceHisBVOService.insertBatchSomeColumn(einvoiceHisBVOList);
    return "插入成功";
  }

  /**
   * 测试自动添加权限控制的批量插入
   *
   * @param einvoiceHisVOList
   * @return
   */
  @GraphQLMutation(name = "saveEinvoiceHisListWithPermission")
  public String einvoiceHisInsertWithPermission(
      @GraphQLArgument(name = "einvoiceHisList") List<EinvoiceHisVO> einvoiceHisVOList) {
    einvoiceHisVOPermissionService.insertBatchSomeColumn(einvoiceHisVOList);
    List<EinvoiceHisBVO> einvoiceHisBVOList = getEinvoiceHisBVOList(einvoiceHisVOList);
    einvoiceHisBVOPermissionService.insertBatchSomeColumn(einvoiceHisBVOList);
    return "插入成功";
  }

  /**
   * 全部删除，删除库中所有数据
   *
   * @param einvoiceHisVOList
   * @return
   */
  @GraphQLMutation(name = "deleteEinvoiceHisList")
  public String einvoiceHisDelete(
      @GraphQLArgument(name = "einvoiceHisLIst") List<EinvoiceHisVO> einvoiceHisVOList) {
    QueryWrapper<EinvoiceHisVO> einvoiceHisVOQueryWrapper = new QueryWrapper<>();
    QueryWrapper<EinvoiceHisBVO> einvoiceHisBVOQueryWrapper = new QueryWrapper<>();
    einvoiceHisVOQueryWrapper.ne("id", "0");
    einvoiceHisBVOQueryWrapper.ne("id", "0");
    einvoiceHisVOService
        .delete(einvoiceHisVOQueryWrapper);
    einvoiceHisBVOService
        .delete(einvoiceHisBVOQueryWrapper);
    return "删除成功";
  }

  /**
   * 全部删除，删除该租户下所有数据
   *
   * @param einvoiceHisVOList
   * @return
   */
  @GraphQLMutation(name = "deleteEinvoiceHisListWithPermission")
  public String einvoiceHisDeleteWithPermission(
      @GraphQLArgument(name = "einvoiceHisList") List<EinvoiceHisVO> einvoiceHisVOList) {
    QueryWrapper<EinvoiceHisVO> einvoiceHisVOQueryWrapper = new QueryWrapper<>();
    QueryWrapper<EinvoiceHisBVO> einvoiceHisBVOQueryWrapper = new QueryWrapper<>();
    einvoiceHisVOQueryWrapper.ne("id", "0");
    einvoiceHisBVOQueryWrapper.ne("id", "0");
    einvoiceHisVOPermissionService
        .delete(einvoiceHisVOQueryWrapper);
    einvoiceHisBVOPermissionService
        .delete(einvoiceHisBVOQueryWrapper);
    return "删除成功";
  }

  private List<EinvoiceHisBVO> getEinvoiceHisBVOList(List<EinvoiceHisVO> einvoiceHisVOList) {
    if (CollectionUtils.isEmpty(einvoiceHisVOList)) {
      return Collections.emptyList();
    }
    einvoiceHisVOList.forEach(einvoiceHisVO -> {
      List<EinvoiceHisBVO> einvoiceHisBVOList = einvoiceHisVO.getEinvoiceHisBVOList();
      if (!CollectionUtils.isEmpty(einvoiceHisBVOList)) {
        einvoiceHisBVOList.forEach(einvoiceHisBVO -> einvoiceHisBVO.setHid(einvoiceHisVO.getId()));
      }
    });
    List<EinvoiceHisBVO> einvoiceHisBVOList = einvoiceHisVOList.stream()
        .filter(einvoiceHisVO -> !CollectionUtils.isEmpty(einvoiceHisVO.getEinvoiceHisBVOList()))
        .flatMap(einvoiceHisVO -> einvoiceHisVO.getEinvoiceHisBVOList().stream())
        .collect(Collectors.toList());
    return einvoiceHisBVOList;
  }

}
