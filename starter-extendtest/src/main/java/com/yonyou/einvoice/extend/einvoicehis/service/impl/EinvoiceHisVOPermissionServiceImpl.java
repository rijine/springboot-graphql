package com.yonyou.einvoice.extend.einvoicehis.service.impl;

import com.yonyou.einvoice.common.agile.service.AbstractMybatisPermissionService;
import com.yonyou.einvoice.extend.einvoicehis.entity.EinvoiceHisVO;
import com.yonyou.einvoice.extend.einvoicehis.repository.EinvoiceHisVOMapper;
import com.yonyou.einvoice.extend.einvoicehis.service.IEinvoiceHisVOService;
import org.springframework.stereotype.Service;


/**
 * einvoice_his
 *
 * @author liuqiangm
 */

@Service
public class EinvoiceHisVOPermissionServiceImpl
    extends AbstractMybatisPermissionService<EinvoiceHisVO, EinvoiceHisVOMapper>
    implements IEinvoiceHisVOService {

}