package mp.entity;

import com.yonyou.einvoice.common.agile.mp.anno.AggDetailIndex;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class MultiLang {

  @AggDetailIndex(aggIndex = "1")
  private String cn;
  @AggDetailIndex(aggIndex = "2")
  private String en;
  @AggDetailIndex(aggIndex = "3")
  private String fr;
  @AggDetailIndex(aggIndex = "4")
  private String jp;
  @AggDetailIndex(aggIndex = "5")
  private String kr;
}
