import com.esotericsoftware.reflectasm.ConstructorAccess;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import org.junit.Test;

/**
 * 测试三种不同的设置属性值方法的性能
 *
 * @author liuqiangm
 */
public class ReflectAsmConstructorTest {

  Class clazz = Person.class;
  private static long totalNum = 3000000000L;

  @Test
  public void testRawNew() throws Exception {
    Person person = new Person();
    long start = System.currentTimeMillis();
    for (long i = 0; i < totalNum; i++) {
      person = new Person();
      person.t();
    }
    long end = System.currentTimeMillis();
    System.out.println("testSetMethod: " + (end - start));
  }

  @Test
  public void rawReflection() throws Exception {
    Constructor constructor = clazz.getConstructor();
    Person person = (Person) constructor.newInstance();
    long start = System.currentTimeMillis();
    for (long i = 0; i < totalNum; i++) {
      person = (Person) constructor.newInstance();
      person.t();
    }
    long end = System.currentTimeMillis();
    System.out.println("rawReflection: " + (end - start));
  }

  @Test
  public void reflectAsm() throws Exception {
    Field field = clazz.getDeclaredField("name");
    ConstructorAccess constructorAccess = ConstructorAccess.get(clazz);
    Person person = (Person) constructorAccess.newInstance();
    long start = System.currentTimeMillis();
    for (long i = 0; i < totalNum; i++) {
      person = (Person) constructorAccess.newInstance();
      person.t();
    }
    long end = System.currentTimeMillis();
    System.out.println("reflectAsm: " + (end - start));
  }
}

