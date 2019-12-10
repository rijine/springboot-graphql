package com.yonyou.einvoice.extend.einvoicehisext.repository;

import com.yonyou.einvoice.common.metadata.mp.anno.ExtensionMeta;
import com.yonyou.einvoice.common.metadata.mp.repository.IExtendMetaMapper;
import com.yonyou.einvoice.extend.einvoicehis.entity.EinvoiceHisVO;
import com.yonyou.einvoice.extend.einvoicehisext.entity.EinvoiceHisExtVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


/**
 * einvoice_his_ext 扩展类Mapper
 * <p>
 * ExtensionMeta注解表明当前Mapper类是扩展自EinvoiceHisVO类
 *
 * @author liuqiangm
 */
@Mapper
@Repository
@ExtensionMeta(entityClazz = EinvoiceHisVO.class)

public interface EinvoiceHisExtVOMapper extends IExtendMetaMapper<EinvoiceHisExtVO> {

}