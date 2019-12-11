package com.yonyou.einvoice.testcase.einvoicehisb.service.impl;

import com.yonyou.einvoice.common.agile.service.AbstractMybatisPermissionService;
import com.yonyou.einvoice.testcase.einvoicehisb.entity.EinvoiceHisBVO;
import com.yonyou.einvoice.testcase.einvoicehisb.repository.EinvoiceHisBVOMapper;
import com.yonyou.einvoice.testcase.einvoicehisb.service.IEinvoiceHisBVOService;
import org.springframework.stereotype.Service;


/**
 * einvoice_his_b
 *
 * @author liuqiangm
 */

@Service
public class EinvoiceHisBVOPermissionServiceImpl
    extends AbstractMybatisPermissionService<EinvoiceHisBVO, EinvoiceHisBVOMapper>
    implements IEinvoiceHisBVOService {

}