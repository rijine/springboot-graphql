import com.esotericsoftware.reflectasm.MethodAccess;
import java.lang.reflect.Field;
import lombok.Getter;
import lombok.Setter;
import org.junit.Test;

/**
 * 测试三种不同的设置属性值方法的性能
 *
 * @author liuqiangm
 */
public class ReflectAsmPropertyTest {

  Class clazz = Person.class;
  private static long totalNum = 3000000000L;

  @Test
  public void testSetMethod() throws Exception {
    Person person = new Person();
    person.setName("123");
    long start = System.currentTimeMillis();
    for (long i = 0; i < totalNum; i++) {
      person.setName("123");
    }
    long end = System.currentTimeMillis();
    System.out.println("testSetMethod: " + (end - start));
  }

  @Test
  public void rawReflection() throws Exception {
    Field field = clazz.getDeclaredField("name");
    field.setAccessible(true);
    Person person = new Person();
    field.set(person, "123");
    long start = System.currentTimeMillis();
    for (long i = 0; i < totalNum; i++) {
      field.set(person, "123");
    }
    long end = System.currentTimeMillis();
    System.out.println("rawReflection: " + (end - start));
  }

  @Test
  public void reflectAsm() throws Exception {
    Field field = clazz.getDeclaredField("name");
    field.setAccessible(true);
    Person person = new Person();
    MethodAccess methodAccess = MethodAccess.get(clazz);
    int index = methodAccess.getIndex("setName");
    long start = System.currentTimeMillis();
    for (long i = 0; i < totalNum; i++) {
      methodAccess.invoke(person, index, "123");
    }
    long end = System.currentTimeMillis();
    System.out.println("reflectAsm: " + (end - start));
  }
}

@Setter
@Getter
class Person {

  private String name;
  public boolean flag;

  public Person() {
  }

  public void t() {
  }
}