package com.yonyou.einvoice.common.metadata.graphql;

import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.stereotype.Service;

@Service
public class GraphQLServiceImpl implements IGraphQLService {

  @GraphQLQuery
  public int hello() {
    return 0;
  }
}
