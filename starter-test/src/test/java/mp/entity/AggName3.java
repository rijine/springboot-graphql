package mp.entity;

import com.yonyou.einvoice.common.agile.mp.anno.AggDetailIndex;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AggName3 {

  @AggDetailIndex(aggIndex = "0")
  private String name0;
  @AggDetailIndex(aggIndex = "1")
  private String name1;
  @AggDetailIndex(aggIndex = "2")
  private String name2;
  @AggDetailIndex(aggIndex = "3")
  private String name3;
  @AggDetailIndex(aggIndex = "4")
  private String name4;
  @AggDetailIndex(aggIndex = "5")
  private String name5;
  @AggDetailIndex(aggIndex = "6")
  private String name6;
}
