package com.yonyou.einvoice.common.metadata.graphiql.tgiql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonyou.einvoice.common.metadata.graphiql.PropertyGroupReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 前后端开发联调的graphql调试窗口
 *
 * @author liuqiangm
 */
@Slf4j
@Controller
public class TestIQLController {

  private static final String CDNJS_CLOUDFLARE_COM_AJAX_LIBS = "//cdnjs.cloudflare.com/ajax/libs/";
  private static final String CDN_JSDELIVR_NET_NPM = "//cdn.jsdelivr.net/npm/";
  private static final String GRAPHIQL = "graphiql";
  private static final String FAVICON_GRAPHQL_ORG = "//graphql.org/img/favicon.png";

  @Autowired
  private Environment environment;

  @Autowired
  private TestIQLProperties testGIQLProperties;

  private String template;
  private String props;
  private Properties headerProperties;

  @PostConstruct
  public void onceConstructed() throws IOException {
    loadTemplate();
    loadProps();
    loadHeaders();
  }

  private void loadTemplate() throws IOException {
    try (InputStream inputStream = new ClassPathResource("graphiql.html").getInputStream()) {
      template = StreamUtils.copyToString(inputStream, Charset.defaultCharset());
    }
  }

  private void loadProps() throws IOException {
    props = new PropsLoader(environment).load();
  }

  private void loadHeaders() {
    PropertyGroupReader propertyReader = new PropertyGroupReader(environment, "graphiql.headers.");
    headerProperties = propertyReader.load();
    addIfAbsent(headerProperties, "Accept");
    addIfAbsent(headerProperties, "Content-Type");
  }

  private void addIfAbsent(Properties headerProperties, String header) {
    if (!headerProperties.containsKey(header)) {
      headerProperties.setProperty(header, MediaType.APPLICATION_JSON_VALUE);
    }
  }

  @GetMapping(value = "${metadata.testiql.mapping:/testiql}")
  public void testgiql(HttpServletRequest request, HttpServletResponse response,
      @PathVariable Map<String, String> params) throws IOException {
    if (testGIQLProperties.isEnable()) {
      response.setContentType("text/html; charset=UTF-8");

      Map<String, String> replacements = getReplacements(
          constructGraphQlEndpoint(request, params),
          request.getContextPath() + testGIQLProperties.getEndpoint().getSubscriptions(),
          request.getContextPath() + testGIQLProperties.getSTATIC().getBasePath()
      );

      String populatedTemplate = StrSubstitutor.replace(template, replacements);
      response.getOutputStream().write(populatedTemplate.getBytes(Charset.defaultCharset()));
    } else {
      response.setCharacterEncoding("UTF-8");
      response.setHeader("Content-Type", "application/json");
      response.setHeader("Access-Control-Allow-Credentials", "true");
      response.setHeader("Access-Control-Allow-Methods", "GET, POST");
      response.setHeader("Access-Control-Allow-Origin", "*");
      response.setHeader("Access-Control-Max-Age", "3600");
      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType("application/json;charset=UTF-8");
      response.getOutputStream().write("{}".getBytes(Charset.defaultCharset()));
    }
  }

  private Map<String, String> getReplacements(
      String graphqlEndpoint,
      String subscriptionsEndpoint,
      String staticBasePath
  ) {
    Map<String, String> replacements = new HashMap<>();
    replacements.put("graphqlEndpoint", graphqlEndpoint);
    replacements.put("subscriptionsEndpoint", subscriptionsEndpoint);
    replacements.put("staticBasePath", staticBasePath);
    replacements.put("pageTitle", testGIQLProperties.getPageTitle());
    replacements
        .put("pageFavicon", getResourceUrl(staticBasePath, "favicon.ico", FAVICON_GRAPHQL_ORG));
    replacements.put("es6PromiseJsUrl", getResourceUrl(staticBasePath, "es6-promise.auto.min.js",
        joinCdnjsPath("es6-promise", "4.1.1", "es6-promise.auto.min.js")));
    replacements.put("fetchJsUrl", getResourceUrl(staticBasePath, "fetch.min.js",
        joinCdnjsPath("fetch", "2.0.4", "fetch.min.js")));
    replacements.put("reactJsUrl", getResourceUrl(staticBasePath, "react.min.js",
        joinCdnjsPath("react", "16.8.3", "umd/react.production.min.js")));
    replacements.put("reactDomJsUrl", getResourceUrl(staticBasePath, "react-dom.min.js",
        joinCdnjsPath("react-dom", "16.8.3", "umd/react-dom.production.min.js")));
    replacements.put("graphiqlCssUrl", getResourceUrl(staticBasePath, "graphiql.min.css",
        joinJsDelivrPath(GRAPHIQL, testGIQLProperties.getCdn().getVersion(), "graphiql.css")));
    replacements.put("graphiqlJsUrl", getResourceUrl(staticBasePath, "graphiql.min.js",
        joinJsDelivrPath(GRAPHIQL, testGIQLProperties.getCdn().getVersion(), "graphiql.min.js")));
    replacements.put("subscriptionsTransportWsBrowserClientUrl", getResourceUrl(staticBasePath,
        "subscriptions-transport-ws-browser-client.js",
        joinJsDelivrPath("subscriptions-transport-ws", "0.9.15", "browser/client.js")));
    replacements.put("graphiqlSubscriptionsFetcherBrowserClientUrl", getResourceUrl(staticBasePath,
        "graphiql-subscriptions-fetcher-browser-client.js",
        joinJsDelivrPath("graphiql-subscriptions-fetcher", "0.0.2", "browser/client.js")));
    replacements.put("props", props);
    try {
      replacements.put("headers", new ObjectMapper().writeValueAsString(headerProperties));
    } catch (JsonProcessingException e) {
      log.error("Cannot serialize headers", e);
    }
    replacements.put("subscriptionClientTimeout",
        String.valueOf(testGIQLProperties.getSubscriptions().getTimeout() * 1000));
    replacements.put("subscriptionClientReconnect",
        String.valueOf(testGIQLProperties.getSubscriptions().isReconnect()));
    replacements.put("editorThemeCss", getEditorThemeCssURL());
    return replacements;
  }

  private String getEditorThemeCssURL() {
    return "";
  }

  private String getResourceUrl(String staticBasePath, String staticFileName, String cdnUrl) {
    if (testGIQLProperties.getCdn().isEnabled() && !StringUtils.isEmpty(cdnUrl)) {
      return cdnUrl;
    }
    return joinStaticPath(staticBasePath, staticFileName);
  }

  private String joinStaticPath(String staticBasePath, String staticFileName) {
    return staticBasePath + "vendor/graphiql/" + staticFileName;
  }

  private String joinCdnjsPath(String library, String cdnVersion, String cdnFileName) {
    return CDNJS_CLOUDFLARE_COM_AJAX_LIBS + library + "/" + cdnVersion + "/" + cdnFileName;
  }

  private String joinJsDelivrPath(String library, String cdnVersion, String cdnFileName) {
    return CDN_JSDELIVR_NET_NPM + library + "@" + cdnVersion + "/" + cdnFileName;
  }

  private String constructGraphQlEndpoint(HttpServletRequest request,
      @RequestParam Map<String, String> params) {
    String endpoint = testGIQLProperties.getEndpoint().getGraphql();
    for (Map.Entry<String, String> param : params.entrySet()) {
      endpoint = endpoint.replaceAll("\\{" + param.getKey() + "}", param.getValue());
    }
    if (!StringUtils.isEmpty(request.getContextPath()) && !endpoint
        .startsWith(request.getContextPath())) {
      return request.getContextPath() + endpoint;
    }
    return endpoint;
  }

}
