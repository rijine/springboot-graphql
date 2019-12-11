package com.github.einvoice.testcase.einvoicehisb.repository;

import com.github.einvoice.testcase.einvoicehisb.entity.EinvoiceHisBVO;
import com.yonyou.einvoice.common.agile.mp.repository.IMetaMapper;
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