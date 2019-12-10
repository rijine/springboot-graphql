package com.github.einvoice.testcase.einvoicehis.repository;

import com.github.einvoice.testcase.einvoicehis.entity.EinvoiceHisVO;
import com.yonyou.einvoice.common.metadata.mp.repository.IMetaMapper;
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