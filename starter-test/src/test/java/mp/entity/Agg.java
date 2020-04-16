package mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@TableName(value = "agg")
@Setter
@Getter
@ToString
public class Agg {

  private Integer id;
  private String name1;
  private String name2;
  private String name3;
  private String name4;
  private String name5;
  private String fpqqlsh;
  private Date createtime;
  private Date ts;
}
