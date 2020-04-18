package mp.entity;

import com.yonyou.einvoice.common.agile.mp.anno.AggField;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class EinvoiceAggEntity {

  private Integer id;
  @AggField(aggPrefix = "name")
  private AggName1 name1;
  @AggField
  private AggName2 name2;
  @AggField
  private AggName3 name;
  @AggField(aggPrefix = "name")
  private MultiLang name3;
  @AggField(aggPrefix = "code")
  private MultiLang code;
  private String fpqqlsh;
  private Date createtime;
  private Date ts;
}
