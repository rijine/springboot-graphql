package com.yonyou.einvoice.extend.einvoicehis.repository;

import com.yonyou.einvoice.common.agile.mp.repository.IMetaMapper;
import com.yonyou.einvoice.extend.einvoicehis.entity.EinvoiceHisVO;
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