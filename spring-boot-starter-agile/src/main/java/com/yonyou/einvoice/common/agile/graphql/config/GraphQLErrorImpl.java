package com.yonyou.einvoice.common.agile.graphql.config;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * graphql错误信息类
 *
 * @author liuqiangm
 */
@Setter
@Getter
public class GraphQLErrorImpl implements GraphQLError {

  private String message;
  /**
   * 用于保存错误信息的路径。 例如：对于graphql的多层查询，若最内层出现exception，则path会从最外层叠加到出现exception的那一层。
   */
  private List<Object> path;

  @Override
  public List<SourceLocation> getLocations() {
    return null;
  }

  @Override
  public ErrorType getErrorType() {
    return null;
  }

  @Override
  public Map<String, Object> toSpecification() {
    return null;
  }

  @Override
  public Map<String, Object> getExtensions() {
    return null;
  }


}
