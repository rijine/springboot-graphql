package com.yonyou.einvoice.extend.einvoicehisb.service.impl;

import com.yonyou.einvoice.common.metadata.service.AbstractMybatisPermissionService;
import com.yonyou.einvoice.extend.einvoicehisb.entity.EinvoiceHisBVO;
import com.yonyou.einvoice.extend.einvoicehisb.repository.EinvoiceHisBVOMapper;
import com.yonyou.einvoice.extend.einvoicehisb.service.IEinvoiceHisBVOService;
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