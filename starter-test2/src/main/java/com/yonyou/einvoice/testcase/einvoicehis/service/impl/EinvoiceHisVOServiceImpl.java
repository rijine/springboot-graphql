package com.yonyou.einvoice.testcase.einvoicehis.service.impl;

import com.yonyou.einvoice.common.metadata.service.AbstractMybatisService;
import com.yonyou.einvoice.testcase.einvoicehis.entity.EinvoiceHisVO;
import com.yonyou.einvoice.testcase.einvoicehis.repository.EinvoiceHisVOMapper;
import com.yonyou.einvoice.testcase.einvoicehis.service.IEinvoiceHisVOService;
import org.springframework.stereotype.Service;


/**
 * einvoice_his
 *
 * @author liuqiangm
 */

@Service
public class EinvoiceHisVOServiceImpl
    extends AbstractMybatisService<EinvoiceHisVO, EinvoiceHisVOMapper>
    implements IEinvoiceHisVOService {

}