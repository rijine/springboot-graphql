package com.yonyou.einvoice.common.metadata.graphiql.tgiql;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * TestGIQL属性
 */
@Setter
@Getter
@ToString
@ConfigurationProperties(prefix = "metadata.testiql")
public class TestIQLProperties {

  private Endpoint endpoint = new Endpoint();
  private Static STATIC = new Static();
  private CodeMirror codeMirror = new CodeMirror();
  //  private Props props = new Props();
  private String pageTitle = "TestIQL";
  private String mapping = "/testiql";
  private Subscriptions subscriptions = new Subscriptions();
  private Cdn cdn = new Cdn();

  private boolean enable = true;

  @Data
  static class Endpoint {

    private String graphql = "/testql";
    private String subscriptions = "/subscriptions";
  }

  @Data
  static class Static {

    private String basePath = "/";
  }

  @Data
  static class CodeMirror {

    private String version = "5.47.0";
  }

  @Data
  static class Props {

    private Variables variables = new Variables();

    @Data
    static class Variables {

      private String editorTheme;
    }
  }

  @Data
  static class Cdn {

    private boolean enabled = true;
    private String version = "0.13.0";
  }

  @Data
  static class Subscriptions {

    private int timeout = 30;
    private boolean reconnect = false;
  }
}
