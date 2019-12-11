package com.github.einvoice.testcase.einvoicehis.service.impl;

import com.github.einvoice.testcase.einvoicehis.entity.EinvoiceHisVO;
import com.github.einvoice.testcase.einvoicehis.repository.EinvoiceHisVOMapper;
import com.github.einvoice.testcase.einvoicehis.service.IEinvoiceHisVOService;
import com.yonyou.einvoice.common.agile.service.AbstractMybatisService;
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