package com.yonyou.einvoice.common.metadata;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "metadata.prop")
@Setter
@Getter
@ToString
public class MetadataProperties {

  /**
   * GraphQL和SPQR框架扫描的包名
   */
  String basepackages;

}
