package com.yonyou.einvoice.extend.einvoicehis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yonyou.einvoice.common.agile.entity.IAgileEntity;
import com.yonyou.einvoice.common.agile.mp.anno.InsertBatchIgnore;
import com.yonyou.einvoice.common.agile.mp.relate.Many;
import com.yonyou.einvoice.extend.einvoicehisb.entity.EinvoiceHisBVO;
import io.leangen.graphql.annotations.GraphQLQuery;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

/**
 * einvoice_his
 *
 * @author liuqiangm
 */
@TableName(value = "einvoice_his", autoResultMap = true)
@Getter
@Setter
@ToString
public class EinvoiceHisVO implements IAgileEntity {

  /**
   * einvoice_his.id 主键
   */
  @TableId(value = "id", type = IdType.AUTO)
  protected Long id;
  /**
   * einvoice_his.fpqqlsh 发票请求流水号
   */
  @NotNull(message = "fpqqlsh字段不能为空")
  @Length(max = 64, message = "fpqqlsh字段长度不能超过64")
  protected String fpqqlsh;
  /**
   * einvoice_his.fplx 发票类型1增值税电子普通发票;3增值税普通发票;4增值税专用发票;8成品油电子发票;9成品油普通发票(卷式);10成品油普通发票;11成品油专用发票;12增值税普通发票(卷式)
   */
  protected String fplx;
  /**
   * einvoice_his.zsfs 征税方式 0-普通征税 2-差额征税
   */
  protected String zsfs;
  /**
   * einvoice_his.kplx 开票类型
   */
  protected Integer kplx;
  /**
   * einvoice_his.fpjz 发票介质:0-电子发票;1-纸质发票。
   */
  protected String fpjz;
  /**
   * einvoice_his.xsf_nsrsbh 销售方纳税人识别号
   */
  protected String xsfNsrsbh;
  /**
   * einvoice_his.xsf_mc 销售方名称
   */
  protected String xsfMc;
  /**
   * einvoice_his.xsf_dzdh 销售方地址、电话
   */
  protected String xsfDzdh;
  /**
   * einvoice_his.xsf_yhzh 销售方银行账号
   */
  protected String xsfYhzh;
  /**
   * einvoice_his.gmf_nsrsbh 购买方纳税人识别号
   */
  protected String gmfNsrsbh;
  /**
   * einvoice_his.gmf_mc 购买方名称
   */
  protected String gmfMc;
  /**
   * einvoice_his.gmf_dzdh 购买方地址、电话
   */
  protected String gmfDzdh;
  /**
   * einvoice_his.gmf_yhzh 购买方银行账号
   */
  protected String gmfYhzh;
  /**
   * einvoice_his.kpr 开票人
   */
  protected String kpr;
  /**
   * einvoice_his.skr 收款人
   */
  protected String skr;
  /**
   * einvoice_his.fhr 复核人
   */
  protected String fhr;
  /**
   * einvoice_his.tschbz 特殊冲红标志
   */
  protected String tschbz;
  /**
   * einvoice_his.yfp_dm 原发票代码
   */
  protected String yfpDm;
  /**
   * einvoice_his.yfp_hm 原发票号码
   */
  protected String yfpHm;
  /**
   * einvoice_his.jshj 价税合计
   */
  protected BigDecimal jshj;
  /**
   * einvoice_his.hjje 合计金额
   */
  protected BigDecimal hjje;
  /**
   * einvoice_his.hjse 合计税额
   */
  protected BigDecimal hjse;
  /**
   * einvoice_his.bz 备注
   */
  protected String bz;
  /**
   * einvoice_his.jqbh 税控设备编号
   */
  protected String jqbh;
  /**
   * einvoice_his.fp_dm 发票代码
   */
  protected String fpDm;
  /**
   * einvoice_his.fp_hm 发票号码
   */
  protected String fpHm;
  /**
   * einvoice_his.kprq 开票日期
   */
  protected String kprq;
  /**
   * einvoice_his.fp_mw 发票密文
   */
  protected String fpMw;
  /**
   * einvoice_his.jym 校验码
   */
  protected String jym;
  /**
   * einvoice_his.ewm 二维码
   */
  protected String ewm;
  /**
   * einvoice_his.zdrq 制单日期
   */
  protected Date zdrq;
  /**
   * einvoice_his.sbbz 失败备注
   */
  protected String sbbz;
  /**
   * einvoice_his.lylx 发票申请来源
   */
  protected String lylx;
  /**
   * einvoice_his.lyid 发票申请来源id
   */
  protected String lyid;
  /**
   * einvoice_his.def1 自定义项
   */
  protected String def1;
  /**
   * einvoice_his.def2
   */
  protected String def2;
  /**
   * einvoice_his.def3
   */
  protected String def3;
  /**
   * einvoice_his.def4
   */
  protected String def4;
  /**
   * einvoice_his.def5
   */
  protected String def5;
  /**
   * einvoice_his.tenantid 租户id
   */
  protected String tenantid;
  /**
   * einvoice_his.creator 创建者
   */
  protected String creator;
  /**
   * einvoice_his.ts 时间戳
   */
  @InsertBatchIgnore
  protected Date ts;
  /**
   * einvoice_his.status 状态：1、已生成版式文件2、已下载3、已入帐
   */
  protected String status;
  /**
   * einvoice_his.bred 是否被红冲
   */
  protected String bred;
  /**
   * einvoice_his.corpid 企业（集团）编码
   */
  protected String corpid;
  /**
   * einvoice_his.orgid 组织编码
   */
  protected Integer orgid;
  /**
   * einvoice_his.bmb_bbh
   */
  protected String bmbBbh;
  /**
   * einvoice_his.sgbz 农产品收购标志；2=农产品收购
   */
  protected String sgbz;
  /**
   * einvoice_his.zdybz 自定义备注
   */
  protected String zdybz;
  /**
   * einvoice_his.createtime 创建时间
   */
  @InsertBatchIgnore
  protected Date createtime;
  /**
   * einvoice_his.zfbz 作废标志；Y=已作废，N-未作废，I=正在作废，F=作废失败（失败原因在失败备注中）
   */
  protected String zfbz;
  /**
   * einvoice_his.account_status 1-未记账；2-已记账
   */
  protected String accountStatus;
  /**
   * einvoice_his.hzxxbbh 红字信息表编号
   */
  protected String hzxxbbh;

  @GraphQLQuery(name = "id", description = "主键")
  public Long id() {
    return this.id;
  }

  @GraphQLQuery(name = "fpqqlsh", description = "发票请求流水号 ")
  public String fpqqlsh() {
    return this.fpqqlsh;
  }

  @GraphQLQuery(name = "fplx", description = "发票类型1增值税电子普通发票;3增值税普通发票;4增值税专用发票;8成品油电子发票;9成品油普通发票(卷式);10成品油普通发票;11成品油专用发票;12增值税普通发票(卷式)")
  public String fplx() {
    return this.fplx;
  }

  @GraphQLQuery(name = "zsfs", description = "征税方式 0-普通征税 2-差额征税")
  public String zsfs() {
    return this.zsfs;
  }

  @GraphQLQuery(name = "kplx", description = "开票类型 ")
  public Integer kplx() {
    return this.kplx;
  }

  @GraphQLQuery(name = "fpjz", description = "发票介质:0-电子发票;1-纸质发票。")
  public String fpjz() {
    return this.fpjz;
  }

  @GraphQLQuery(name = "xsfNsrsbh", description = "销售方纳税人识别号")
  public String xsfNsrsbh() {
    return this.xsfNsrsbh;
  }

  @GraphQLQuery(name = "xsfMc", description = "销售方名称")
  public String xsfMc() {
    return this.xsfMc;
  }

  @GraphQLQuery(name = "xsfDzdh", description = "销售方地址、电话")
  public String xsfDzdh() {
    return this.xsfDzdh;
  }

  @GraphQLQuery(name = "xsfYhzh", description = "销售方银行账号")
  public String xsfYhzh() {
    return this.xsfYhzh;
  }

  @GraphQLQuery(name = "gmfNsrsbh", description = "购买方纳税人识别号")
  public String gmfNsrsbh() {
    return this.gmfNsrsbh;
  }

  @GraphQLQuery(name = "gmfMc", description = "购买方名称")
  public String gmfMc() {
    return this.gmfMc;
  }

  @GraphQLQuery(name = "gmfDzdh", description = "购买方地址、电话")
  public String gmfDzdh() {
    return this.gmfDzdh;
  }

  @GraphQLQuery(name = "gmfYhzh", description = "购买方银行账号")
  public String gmfYhzh() {
    return this.gmfYhzh;
  }

  @GraphQLQuery(name = "kpr", description = "开票人")
  public String kpr() {
    return this.kpr;
  }

  @GraphQLQuery(name = "skr", description = "收款人")
  public String skr() {
    return this.skr;
  }

  @GraphQLQuery(name = "fhr", description = "复核人")
  public String fhr() {
    return this.fhr;
  }

  @GraphQLQuery(name = "tschbz", description = "特殊冲红标志")
  public String tschbz() {
    return this.tschbz;
  }

  @GraphQLQuery(name = "yfpDm", description = "原发票代码")
  public String yfpDm() {
    return this.yfpDm;
  }

  @GraphQLQuery(name = "yfpHm", description = "原发票号码")
  public String yfpHm() {
    return this.yfpHm;
  }

  @GraphQLQuery(name = "jshj", description = "价税合计")
  public BigDecimal jshj() {
    return this.jshj;
  }

  @GraphQLQuery(name = "hjje", description = "合计金额")
  public BigDecimal hjje() {
    return this.hjje;
  }

  @GraphQLQuery(name = "hjse", description = "合计税额")
  public BigDecimal hjse() {
    return this.hjse;
  }

  @GraphQLQuery(name = "bz", description = "备注")
  public String bz() {
    return this.bz;
  }

  @GraphQLQuery(name = "jqbh", description = "税控设备编号")
  public String jqbh() {
    return this.jqbh;
  }

  @GraphQLQuery(name = "fpDm", description = "发票代码")
  public String fpDm() {
    return this.fpDm;
  }

  @GraphQLQuery(name = "fpHm", description = "发票号码")
  public String fpHm() {
    return this.fpHm;
  }

  @GraphQLQuery(name = "kprq", description = "开票日期")
  public String kprq() {
    return this.kprq;
  }

  @GraphQLQuery(name = "fpMw", description = "发票密文")
  public String fpMw() {
    return this.fpMw;
  }

  @GraphQLQuery(name = "jym", description = "校验码")
  public String jym() {
    return this.jym;
  }

  @GraphQLQuery(name = "ewm", description = "二维码")
  public String ewm() {
    return this.ewm;
  }

  @GraphQLQuery(name = "zdrq", description = "制单日期")
  public Date zdrq() {
    return this.zdrq;
  }

  @GraphQLQuery(name = "sbbz", description = "失败备注")
  public String sbbz() {
    return this.sbbz;
  }

  @GraphQLQuery(name = "lylx", description = "发票申请来源")
  public String lylx() {
    return this.lylx;
  }

  @GraphQLQuery(name = "lyid", description = "发票申请来源id")
  public String lyid() {
    return this.lyid;
  }

  @GraphQLQuery(name = "def1", description = "自定义项")
  public String def1() {
    return this.def1;
  }

  @GraphQLQuery(name = "def2", description = "")
  public String def2() {
    return this.def2;
  }

  @GraphQLQuery(name = "def3", description = "")
  public String def3() {
    return this.def3;
  }

  @GraphQLQuery(name = "def4", description = "")
  public String def4() {
    return this.def4;
  }

  @GraphQLQuery(name = "def5", description = "")
  public String def5() {
    return this.def5;
  }

  @GraphQLQuery(name = "tenantid", description = "租户id")
  public String tenantid() {
    return this.tenantid;
  }

  @GraphQLQuery(name = "creator", description = "创建者")
  public String creator() {
    return this.creator;
  }

  @GraphQLQuery(name = "ts", description = "时间戳")
  public Date ts() {
    return this.ts;
  }

  @GraphQLQuery(name = "status", description = "状态：1、已生成版式文件2、已下载3、已入帐")
  public String status() {
    return this.status;
  }

  @GraphQLQuery(name = "bred", description = "是否被红冲")
  public String bred() {
    return this.bred;
  }

  @GraphQLQuery(name = "corpid", description = "企业（集团）编码")
  public String corpid() {
    return this.corpid;
  }

  @GraphQLQuery(name = "orgid", description = "组织编码")
  public Integer orgid() {
    return this.orgid;
  }

  @GraphQLQuery(name = "bmbBbh", description = "")
  public String bmbBbh() {
    return this.bmbBbh;
  }

  @GraphQLQuery(name = "sgbz", description = "农产品收购标志；2=农产品收购")
  public String sgbz() {
    return this.sgbz;
  }

  @GraphQLQuery(name = "zdybz", description = "自定义备注")
  public String zdybz() {
    return this.zdybz;
  }

  @GraphQLQuery(name = "createtime", description = "创建时间")
  public Date createtime() {
    return this.createtime;
  }

  @GraphQLQuery(name = "zfbz", description = "作废标志；Y=已作废，N-未作废，I=正在作废，F=作废失败（失败原因在失败备注中）")
  public String zfbz() {
    return this.zfbz;
  }

  @GraphQLQuery(name = "accountStatus", description = "1-未记账；2-已记账")
  public String accountStatus() {
    return this.accountStatus;
  }

  @GraphQLQuery(name = "hzxxbbh", description = "红字信息表编号")
  public String hzxxbbh() {
    return this.hzxxbbh;
  }

  /**
   * 子表详情列表，注解表明该字段非数据库表字段
   */
  @TableField(exist = false)
  @Many
  protected List<EinvoiceHisBVO> einvoiceHisBVOList;

}