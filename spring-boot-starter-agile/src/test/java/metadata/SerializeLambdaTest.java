package metadata;

import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.sun.tools.corba.se.idl.InterfaceGen;
import java.io.Serializable;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.SerializedLambda;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.util.LambdaUtil;
import org.junit.Test;

public class SerializeLambdaTest {

  @Test
  public void test1() {
    SFunction<Integer, ?> sFunction = this::getString;
    Person person = new Person();

    ISetter setter = person::setName;

  }

  private String getString(Integer integer) {
    return "hello";
  }

  public static SerializedLambda getSerializedMLambda(Serializable serializable) {
    Method method = serializable.getClass().getDeclaredMethod()
  }
}


@FunctionalInterface
interface IGetter<T> extends Serializable {
  Object get(T source);
}

@FunctionalInterface
interface ISetter<T, U> extends Serializable {
  void set(T t, U u);
}

class Person {
  private String name = "hello";
  private String age = "world";

  public void setName(Object o, Object o1) {
  }
}