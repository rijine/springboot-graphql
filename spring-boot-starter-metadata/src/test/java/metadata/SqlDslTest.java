package metadata;

import org.apache.ibatis.jdbc.SQL;
import org.junit.Test;

public class SqlDslTest {

  @Test
  public void sqlDsl() {
    String sql = new SQL().SELECT("id")
        .FROM("einvoice_his")
        .WHERE("id = 1234").AND()
        .WHERE("id = 3455").OR()
        .WHERE("id = 5656")
        .AND()
        .WHERE("id = 858")
        .toString();
    System.out.println(sql);
  }
}
