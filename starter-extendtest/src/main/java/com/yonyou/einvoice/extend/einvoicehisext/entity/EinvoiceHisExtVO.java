package com.yonyou.einvoice.extend.einvoicehisext.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yonyou.einvoice.common.metadata.mp.anno.InsertBatchIgnore;
import com.yonyou.einvoice.extend.einvoicehis.entity.EinvoiceHisVO;
import io.leangen.graphql.annotations.GraphQLQuery;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * einvoice_his_ext
 *
 * @author liuqiangm
 */
@TableName("einvoice_his_ext")
@Getter
@Setter
@ToString
public class EinvoiceHisExtVO extends EinvoiceHisVO {

  /**
   * einvoice_his_ext.ext_id 扩展表-主键
   */
  @TableId(value = "ext_id")
  protected Long extId;
  /**
   * einvoice_his_ext.ext_tenantid 扩展表-租户id
   */
  protected String extTenantid;
  /**
   * einvoice_his_ext.ext_ts 扩展表-时间戳
   */
  @InsertBatchIgnore
  protected Date extTs;

  @GraphQLQuery(name = "extId", description = "扩展表-主键")
  public Long extId() {
    return this.extId;
  }

  @GraphQLQuery(name = "extTenantid", description = "扩展表-租户id")
  public String extTenantid() {
    return this.extTenantid;
  }

  @GraphQLQuery(name = "extTs", description = "扩展表-时间戳")
  public Date extTs() {
    return this.extTs;
  }
}