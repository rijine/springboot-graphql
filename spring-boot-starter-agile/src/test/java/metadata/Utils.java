package metadata;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import metadata.mybatis.ConditionTest;

public class Utils {

  private static JSON getJSON(String path) {
    try {
      return JSONObject
          .parseObject(ConditionTest.class.getClassLoader().getResourceAsStream(path),
              JSONObject.class);
    } catch (Exception e) {
      return new JSONObject();
    }
  }

  public static <T> T getTObject(String path, Class<T> clazz) {
    JSON json = getJSON(path);
    return JSON.toJavaObject(json, clazz);
  }
}
