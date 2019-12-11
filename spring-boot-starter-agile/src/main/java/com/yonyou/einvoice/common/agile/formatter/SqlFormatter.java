package com.yonyou.einvoice.common.agile.formatter;

import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * sql格式化器，用于格式化sql打印
 *
 * @author liuqiangm
 */
public class SqlFormatter {

  private StringBuilder stringBuilder = new StringBuilder(256);

  private int depth = 0;

  private Set<String> keySet = Stream
      .of("select", "from", "where", "group", "having", "order", "limit").collect(
          Collectors.toSet());

  public static String formatSql(String sql) {
    SqlFormatter formatter = new SqlFormatter();
    SqlFormatter sqlFormatter = new SqlFormatter();
    sqlFormatter.process(sql);
    return sqlFormatter.stringBuilder.toString();
  }

  private void process(String sql) {
    StringTokenizer stringTokenizer = new StringTokenizer(sql, " ");
    while (stringTokenizer.hasMoreElements()) {
      String token = stringTokenizer.nextToken();
      if (keySet.contains(token)) {
        printTab();
      } else if ("(".equals(token)) {
        depth++;
        printTab();
      } else if (")".equals(token)) {
        printTab();
        depth--;
      }
      stringBuilder.append(token).append(" ");
    }
  }

  private void printTab() {
    stringBuilder.append("\n");
    for (int i = 0; i < depth; i++) {
      stringBuilder.append("\t");
    }
  }
}
