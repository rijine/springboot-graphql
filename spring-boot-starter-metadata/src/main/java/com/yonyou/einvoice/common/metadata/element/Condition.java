package com.yonyou.einvoice.common.metadata.element;

import com.yonyou.einvoice.common.metadata.enums.OperatorEnum;
import com.yonyou.einvoice.common.metadata.visitor.IMetaElement;
import com.yonyou.einvoice.common.metadata.visitor.IVisitor;
import io.leangen.graphql.annotations.GraphQLNonNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

@Setter
@Getter
public class Condition implements IMetaElement {

  /**
   * exists子句。由于exists内部为一个select语句，因此此处为Source类
   */
  private Source exists;

  @GraphQLNonNull
  private Field sourceField;
  /**
   * 比较操作类型
   */
  @GraphQLNonNull
  private OperatorEnum operator;
  /**
   * 如果是AND 或 OR，则conditionList非空。
   */
  private List<Condition> conditionList = new ArrayList<>();

  private Value v1;
  private Value v2;

  @Override
  public void accept(IVisitor visitor) {
    if (exists != null) {
      visitor.visit(exists);
      return;
    }
    if (sourceField != null) {
      visitor.visit(sourceField);
    }
    if (!CollectionUtils.isEmpty(conditionList)) {
      conditionList.forEach(condition -> visitor.visit(condition));
    }
    if (v1 != null) {
      visitor.visit(v1);
    }
    if (v2 != null) {
      visitor.visit(v2);
    }
  }
}
