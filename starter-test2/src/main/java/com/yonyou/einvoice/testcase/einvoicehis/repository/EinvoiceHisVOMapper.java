package com.yonyou.einvoice.testcase.einvoicehis.repository;

import com.yonyou.einvoice.common.metadata.mp.repository.IMetaMapper;
import com.yonyou.einvoice.testcase.einvoicehis.entity.EinvoiceHisVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


/**
 * einvoice_his
 *
 * @author liuqiangm
 */
@Mapper
@Repository
public interface EinvoiceHisVOMapper extends IMetaMapper<EinvoiceHisVO> {

}