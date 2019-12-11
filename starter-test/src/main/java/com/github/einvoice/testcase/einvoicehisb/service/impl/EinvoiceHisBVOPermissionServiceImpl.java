package com.github.einvoice.testcase.einvoicehisb.service.impl;

import com.github.einvoice.testcase.einvoicehisb.entity.EinvoiceHisBVO;
import com.github.einvoice.testcase.einvoicehisb.repository.EinvoiceHisBVOMapper;
import com.github.einvoice.testcase.einvoicehisb.service.IEinvoiceHisBVOService;
import com.yonyou.einvoice.common.agile.service.AbstractMybatisPermissionService;
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