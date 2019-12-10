package com.yonyou.einvoice.common.metadata.element;

import com.yonyou.einvoice.common.metadata.visitor.IMetaElement;
import com.yonyou.einvoice.common.metadata.visitor.IVisitor;
import io.leangen.graphql.annotations.GraphQLNonNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

/**
 * Having子句
 *
 * @author liuqiangm
 */
@Setter
@Getter
public class Having implements IMetaElement {

  @GraphQLNonNull
  List<Condition> conditionList = new ArrayList<>();

  @Override
  public void accept(IVisitor visitor) {
    if (CollectionUtils.isEmpty(conditionList)) {
      throw new RuntimeException("where子句中至少包含一个查询条件");
    }
    /**
     * 此处只需对conditions添加访问者模式的判断。
     * authConditions为在其他visitor中添加的，无需在此处添加visit
     */
    conditionList.forEach(condition -> visitor.visit(condition));
  }

}
