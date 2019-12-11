package com.github.einvoice.testcase.einvoicehisb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yonyou.einvoice.common.agile.mp.anno.InsertBatchIgnore;
import io.leangen.graphql.annotations.GraphQLQuery;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * einvoice_his_b
 *
 * @author liuqiangm
 */
@TableName(value = "einvoice_his_b", autoResultMap = true)
@Getter
@Setter
@ToString
public class EinvoiceHisBVO {

  /**
   * einvoice_his_b.id 主键
   */
  @TableId(value = "id", type = IdType.AUTO)
  protected Long id;
  /**
   * einvoice_his_b.hid 发票申请表头id
   */
  protected Long hid;
  /**
   * einvoice_his_b.fphxz 发票行性质
   */
  protected Integer fphxz;
  /**
   * einvoice_his_b.xmmc 项目名称
   */
  protected String xmmc;
  /**
   * einvoice_his_b.dw 计量单位
   */
  protected String dw;
  /**
   * einvoice_his_b.ggxh 规格型号
   */
  protected String ggxh;
  /**
   * einvoice_his_b.xmsl 项目数量
   */
  protected BigDecimal xmsl;
  /**
   * einvoice_his_b.xmdj 项目单价
   */
  protected BigDecimal xmdj;
  /**
   * einvoice_his_b.xmhsdj 项目含税单价
   */
  protected BigDecimal xmhsdj;
  /**
   * einvoice_his_b.xmje 项目金额
   */
  protected BigDecimal xmje;
  /**
   * einvoice_his_b.sl 税率
   */
  protected BigDecimal sl;
  /**
   * einvoice_his_b.se 税额
   */
  protected BigDecimal se;
  /**
   * einvoice_his_b.kce 扣除额
   */
  protected BigDecimal kce;
  /**
   * einvoice_his_b.hsbz
   */
  protected String hsbz;
  /**
   * einvoice_his_b.xmjshj 项目价税合计
   */
  protected BigDecimal xmjshj;
  /**
   * einvoice_his_b.hh 行号
   */
  protected String hh;
  /**
   * einvoice_his_b.zkhhh 折扣行行号
   */
  protected String zkhhh;
  /**
   * einvoice_his_b.spbm 商品税收分类编码 末级
   */
  protected String spbm;
  /**
   * einvoice_his_b.zxbm
   */
  protected String zxbm;
  /**
   * einvoice_his_b.yhzcbs
   */
  protected Integer yhzcbs;
  /**
   * einvoice_his_b.lslbs
   */
  protected String lslbs;
  /**
   * einvoice_his_b.zzstsgl
   */
  protected String zzstsgl;
  /**
   * einvoice_his_b.ysxmmc 原始项目名称（不带分类）
   */
  protected String ysxmmc;
  /**
   * einvoice_his_b.corpid
   */
  protected String corpid;
  /**
   * einvoice_his_b.createtime 创建时间
   */
  @InsertBatchIgnore
  protected Date createtime;
  /**
   * einvoice_his_b.ts 时间戳
   */
  @InsertBatchIgnore
  protected Date ts;

  @GraphQLQuery(name = "id", description = "主键")
  public Long id() {
    return this.id;
  }

  @GraphQLQuery(name = "hid", description = "发票申请表头id")
  public Long hid() {
    return this.hid;
  }

  @GraphQLQuery(name = "fphxz", description = "发票行性质")
  public Integer fphxz() {
    return this.fphxz;
  }

  @GraphQLQuery(name = "xmmc", description = "项目名称")
  public String xmmc() {
    return this.xmmc;
  }

  @GraphQLQuery(name = "dw", description = "计量单位")
  public String dw() {
    return this.dw;
  }

  @GraphQLQuery(name = "ggxh", description = "规格型号")
  public String ggxh() {
    return this.ggxh;
  }

  @GraphQLQuery(name = "xmsl", description = "项目数量")
  public BigDecimal xmsl() {
    return this.xmsl;
  }

  @GraphQLQuery(name = "xmdj", description = "项目单价")
  public BigDecimal xmdj() {
    return this.xmdj;
  }

  @GraphQLQuery(name = "xmhsdj", description = "项目含税单价")
  public BigDecimal xmhsdj() {
    return this.xmhsdj;
  }

  @GraphQLQuery(name = "xmje", description = "项目金额")
  public BigDecimal xmje() {
    return this.xmje;
  }

  @GraphQLQuery(name = "sl", description = "税率")
  public BigDecimal sl() {
    return this.sl;
  }

  @GraphQLQuery(name = "se", description = "税额")
  public BigDecimal se() {
    return this.se;
  }

  @GraphQLQuery(name = "kce", description = "扣除额")
  public BigDecimal kce() {
    return this.kce;
  }

  @GraphQLQuery(name = "hsbz", description = "")
  public String hsbz() {
    return this.hsbz;
  }

  @GraphQLQuery(name = "xmjshj", description = "项目价税合计")
  public BigDecimal xmjshj() {
    return this.xmjshj;
  }

  @GraphQLQuery(name = "hh", description = "行号")
  public String hh() {
    return this.hh;
  }

  @GraphQLQuery(name = "zkhhh", description = "折扣行行号")
  public String zkhhh() {
    return this.zkhhh;
  }

  @GraphQLQuery(name = "spbm", description = "商品税收分类编码 末级")
  public String spbm() {
    return this.spbm;
  }

  @GraphQLQuery(name = "zxbm", description = "")
  public String zxbm() {
    return this.zxbm;
  }

  @GraphQLQuery(name = "yhzcbs", description = "")
  public Integer yhzcbs() {
    return this.yhzcbs;
  }

  @GraphQLQuery(name = "lslbs", description = "")
  public String lslbs() {
    return this.lslbs;
  }

  @GraphQLQuery(name = "zzstsgl", description = "")
  public String zzstsgl() {
    return this.zzstsgl;
  }

  @GraphQLQuery(name = "ysxmmc", description = "原始项目名称（不带分类）")
  public String ysxmmc() {
    return this.ysxmmc;
  }

  @GraphQLQuery(name = "corpid", description = "")
  public String corpid() {
    return this.corpid;
  }

  @GraphQLQuery(name = "createtime", description = "创建时间")
  public Date createtime() {
    return this.createtime;
  }

  @GraphQLQuery(name = "ts", description = "时间戳")
  public Date ts() {
    return this.ts;
  }
}