package com.yonyou.einvoice.testcase.einvoicehisb.repository;

import com.yonyou.einvoice.common.metadata.mp.repository.IMetaMapper;
import com.yonyou.einvoice.testcase.einvoicehisb.entity.EinvoiceHisBVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


/**
 * einvoice_his_b
 *
 * @author liuqiangm
 */
@Mapper
@Repository
public interface EinvoiceHisBVOMapper extends IMetaMapper<EinvoiceHisBVO> {

}