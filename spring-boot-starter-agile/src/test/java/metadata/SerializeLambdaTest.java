package metadata;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.junit.Test;

public class SerializeLambdaTest {

  @Test
  public void test1() {
    SFunction<Integer, ?> sFunction = this::getString;
  }

  private String getString(Integer integer) {
    return "hello";
  }

}