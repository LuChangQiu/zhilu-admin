package com.zl.mjga.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zl.mjga.aspect.LoggingAspect;
import com.zl.mjga.repository.UserRepository;
import com.zl.mjga.service.AopLogService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoggingAspect - generateCurlCommand 方法测试")
class LoggingAspectCurlGenerationTest {

  @Mock private AopLogService aopLogService;
  @Mock private ObjectMapper objectMapper;
  @Mock private UserRepository userRepository;
  @Mock private HttpServletRequest request;

  private LoggingAspect loggingAspect;

  @BeforeEach
  void setUp() {
    loggingAspect = new LoggingAspect(aopLogService, objectMapper, userRepository);
  }

  @Nested
  @DisplayName("GET 请求测试")
  class GetRequestTests {

    @Test
    @DisplayName("基本 GET 请求 - 无查询参数")
    void generateCurlCommand_givenBasicGetRequest_shouldGenerateCorrectCurl() {
      // arrange
      when(request.getMethod()).thenReturn("GET");
      when(request.getScheme()).thenReturn("http");
      when(request.getServerName()).thenReturn("localhost");
      when(request.getServerPort()).thenReturn(8080);
      when(request.getRequestURI()).thenReturn("/api/users");
      when(request.getQueryString()).thenReturn(null);
      when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());

      // action
      String result = loggingAspect.generateCurlCommand(request);

      // assert
      assertThat(result).isEqualTo("curl -X GET 'http://localhost:8080/api/users'");
    }

    @Test
    @DisplayName("GET 请求 - 包含查询参数")
    void generateCurlCommand_givenGetRequestWithQueryParams_shouldIncludeQueryString() {
      // arrange
      when(request.getMethod()).thenReturn("GET");
      when(request.getScheme()).thenReturn("http");
      when(request.getServerName()).thenReturn("localhost");
      when(request.getServerPort()).thenReturn(8080);
      when(request.getRequestURI()).thenReturn("/api/users");
      when(request.getQueryString()).thenReturn("page=1&size=10&name=test");
      when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());

      // action
      String result = loggingAspect.generateCurlCommand(request);

      // assert
      assertThat(result)
          .isEqualTo("curl -X GET 'http://localhost:8080/api/users?page=1&size=10&name=test'");
    }

    @Test
    @DisplayName("GET 请求 - HTTPS 协议")
    void generateCurlCommand_givenHttpsGetRequest_shouldUseHttpsScheme() {
      // arrange
      when(request.getMethod()).thenReturn("GET");
      when(request.getScheme()).thenReturn("https");
      when(request.getServerName()).thenReturn("api.example.com");
      when(request.getServerPort()).thenReturn(443);
      when(request.getRequestURI()).thenReturn("/api/users");
      when(request.getQueryString()).thenReturn(null);
      when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());

      // action
      String result = loggingAspect.generateCurlCommand(request);

      // assert
      assertThat(result).isEqualTo("curl -X GET 'https://api.example.com/api/users'");
    }

    @Test
    @DisplayName("GET 请求 - 自定义端口")
    void generateCurlCommand_givenGetRequestWithCustomPort_shouldIncludePort() {
      // arrange
      when(request.getMethod()).thenReturn("GET");
      when(request.getScheme()).thenReturn("http");
      when(request.getServerName()).thenReturn("localhost");
      when(request.getServerPort()).thenReturn(9090);
      when(request.getRequestURI()).thenReturn("/api/users");
      when(request.getQueryString()).thenReturn(null);
      when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());

      // action
      String result = loggingAspect.generateCurlCommand(request);

      // assert
      assertThat(result).isEqualTo("curl -X GET 'http://localhost:9090/api/users'");
    }
  }

  @Nested
  @DisplayName("POST 请求测试")
  class PostRequestTests {

    @Test
    @DisplayName("POST 请求 - JSON 请求体")
    void generateCurlCommand_givenPostRequestWithJsonBody_shouldIncludeDataFlag()
        throws IOException {
      // arrange
      String jsonBody = "{\"name\":\"test\",\"age\":25}";
      setupPostRequest(jsonBody, "application/json");

      // action
      String result = loggingAspect.generateCurlCommand(request);

      // assert
      assertThat(result)
          .contains("curl -X POST")
          .contains("'http://localhost:8080/api/users'")
          .contains("-d '{\"name\":\"test\",\"age\":25}'");
    }

    @Test
    @DisplayName("POST 请求 - 空 JSON 请求体")
    void generateCurlCommand_givenPostRequestWithEmptyJsonBody_shouldNotIncludeDataFlag()
        throws IOException {
      // arrange
      setupPostRequest("", "application/json");

      // action
      String result = loggingAspect.generateCurlCommand(request);

      // assert
      assertThat(result)
          .contains("curl -X POST")
          .contains("'http://localhost:8080/api/users'")
          .doesNotContain("-d");
    }

    @Test
    @DisplayName("POST 请求 - 包含单引号的 JSON")
    void generateCurlCommand_givenPostRequestWithQuotesInJson_shouldEscapeQuotes()
        throws IOException {
      // arrange
      String jsonBody = "{\"message\":\"It's a test\"}";
      setupPostRequest(jsonBody, "application/json");

      // action
      String result = loggingAspect.generateCurlCommand(request);

      // assert
      assertThat(result).contains("-d '{\"message\":\"It\\'s a test\"}'");
    }

    private void setupPostRequest(String body, String contentType) throws IOException {
      when(request.getMethod()).thenReturn("POST");
      when(request.getScheme()).thenReturn("http");
      when(request.getServerName()).thenReturn("localhost");
      when(request.getServerPort()).thenReturn(8080);
      when(request.getRequestURI()).thenReturn("/api/users");
      when(request.getQueryString()).thenReturn(null);
      when(request.getContentType()).thenReturn(contentType);
      when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());

      if (body != null && !body.trim().isEmpty()) {
        BufferedReader reader = new BufferedReader(new StringReader(body));
        when(request.getReader()).thenReturn(reader);
      } else {
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("")));
      }
    }
  }

  @Nested
  @DisplayName("PUT 和 PATCH 请求测试")
  class PutAndPatchRequestTests {

    @Test
    @DisplayName("PUT 请求 - JSON 请求体")
    void generateCurlCommand_givenPutRequestWithJsonBody_shouldIncludeDataFlag()
        throws IOException {
      // arrange
      String jsonBody = "{\"id\":1,\"name\":\"updated\"}";
      setupRequestWithBody("PUT", jsonBody, "application/json");

      // action
      String result = loggingAspect.generateCurlCommand(request);

      // assert
      assertThat(result).contains("curl -X PUT").contains("-d '{\"id\":1,\"name\":\"updated\"}'");
    }

    @Test
    @DisplayName("PATCH 请求 - JSON 请求体")
    void generateCurlCommand_givenPatchRequestWithJsonBody_shouldIncludeDataFlag()
        throws IOException {
      // arrange
      String jsonBody = "{\"name\":\"patched\"}";
      setupRequestWithBody("PATCH", jsonBody, "application/json");

      // action
      String result = loggingAspect.generateCurlCommand(request);

      // assert
      assertThat(result).contains("curl -X PATCH").contains("-d '{\"name\":\"patched\"}'");
    }

    private void setupRequestWithBody(String method, String body, String contentType)
        throws IOException {
      when(request.getMethod()).thenReturn(method);
      when(request.getScheme()).thenReturn("http");
      when(request.getServerName()).thenReturn("localhost");
      when(request.getServerPort()).thenReturn(8080);
      when(request.getRequestURI()).thenReturn("/api/users/1");
      when(request.getQueryString()).thenReturn(null);
      when(request.getContentType()).thenReturn(contentType);
      when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());

      BufferedReader reader = new BufferedReader(new StringReader(body));
      when(request.getReader()).thenReturn(reader);
    }
  }

  @Nested
  @DisplayName("表单数据请求测试")
  class FormDataRequestTests {

    @Test
    @DisplayName("POST 请求 - 表单数据")
    void generateCurlCommand_givenPostRequestWithFormData_shouldIncludeFormData() {
      // arrange
      when(request.getMethod()).thenReturn("POST");
      when(request.getScheme()).thenReturn("http");
      when(request.getServerName()).thenReturn("localhost");
      when(request.getServerPort()).thenReturn(8080);
      when(request.getRequestURI()).thenReturn("/api/login");
      when(request.getQueryString()).thenReturn(null);
      when(request.getContentType()).thenReturn("application/x-www-form-urlencoded");
      when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());

      List<String> paramNames = new ArrayList<>();
      paramNames.add("username");
      paramNames.add("password");
      when(request.getParameterNames()).thenReturn(Collections.enumeration(paramNames));
      when(request.getParameterValues("username")).thenReturn(new String[] {"testuser"});
      when(request.getParameterValues("password")).thenReturn(new String[] {"testpass"});

      // action
      String result = loggingAspect.generateCurlCommand(request);

      // assert
      assertThat(result)
          .contains("curl -X POST")
          .contains("'http://localhost:8080/api/login'")
          .contains("-d 'username=testuser&password=testpass'");
    }

    @Test
    @DisplayName("POST 请求 - 多值表单参数")
    void generateCurlCommand_givenPostRequestWithMultiValueFormData_shouldIncludeAllValues() {
      // arrange
      when(request.getMethod()).thenReturn("POST");
      when(request.getScheme()).thenReturn("http");
      when(request.getServerName()).thenReturn("localhost");
      when(request.getServerPort()).thenReturn(8080);
      when(request.getRequestURI()).thenReturn("/api/submit");
      when(request.getQueryString()).thenReturn(null);
      when(request.getContentType()).thenReturn("application/x-www-form-urlencoded");
      when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());

      List<String> paramNames = new ArrayList<>();
      paramNames.add("tags");
      when(request.getParameterNames()).thenReturn(Collections.enumeration(paramNames));
      when(request.getParameterValues("tags")).thenReturn(new String[] {"tag1", "tag2", "tag3"});

      // action
      String result = loggingAspect.generateCurlCommand(request);

      // assert
      assertThat(result).contains("-d 'tags=tag1&tags=tag2&tags=tag3'");
    }

    @Test
    @DisplayName("POST 请求 - 空表单数据")
    void generateCurlCommand_givenPostRequestWithEmptyFormData_shouldNotIncludeDataFlag() {
      // arrange
      when(request.getMethod()).thenReturn("POST");
      when(request.getScheme()).thenReturn("http");
      when(request.getServerName()).thenReturn("localhost");
      when(request.getServerPort()).thenReturn(8080);
      when(request.getRequestURI()).thenReturn("/api/submit");
      when(request.getQueryString()).thenReturn(null);
      when(request.getContentType()).thenReturn("application/x-www-form-urlencoded");
      when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
      when(request.getParameterNames()).thenReturn(Collections.emptyEnumeration());

      // action
      String result = loggingAspect.generateCurlCommand(request);

      // assert
      assertThat(result)
          .contains("curl -X POST")
          .contains("'http://localhost:8080/api/submit'")
          .doesNotContain("-d");
    }
  }

  @Nested
  @DisplayName("请求头处理测试")
  class HeaderProcessingTests {

    @Test
    @DisplayName("包含常规请求头")
    void generateCurlCommand_givenRequestWithHeaders_shouldIncludeHeaders() {
      // arrange
      when(request.getMethod()).thenReturn("GET");
      when(request.getScheme()).thenReturn("http");
      when(request.getServerName()).thenReturn("localhost");
      when(request.getServerPort()).thenReturn(8080);
      when(request.getRequestURI()).thenReturn("/api/users");
      when(request.getQueryString()).thenReturn(null);

      List<String> headerNames = new ArrayList<>();
      headerNames.add("Authorization");
      headerNames.add("Content-Type");
      headerNames.add("Accept");
      when(request.getHeaderNames()).thenReturn(Collections.enumeration(headerNames));
      when(request.getHeader("Authorization")).thenReturn("Bearer token123");
      when(request.getHeader("Content-Type")).thenReturn("application/json");
      when(request.getHeader("Accept")).thenReturn("application/json");

      // action
      String result = loggingAspect.generateCurlCommand(request);

      // assert
      assertThat(result)
          .contains("-H 'Authorization: Bearer token123'")
          .contains("-H 'Content-Type: application/json'")
          .contains("-H 'Accept: application/json'");
    }

    @Test
    @DisplayName("跳过特定请求头")
    void generateCurlCommand_givenRequestWithSkippedHeaders_shouldExcludeSkippedHeaders() {
      // arrange
      when(request.getMethod()).thenReturn("GET");
      when(request.getScheme()).thenReturn("http");
      when(request.getServerName()).thenReturn("localhost");
      when(request.getServerPort()).thenReturn(8080);
      when(request.getRequestURI()).thenReturn("/api/users");
      when(request.getQueryString()).thenReturn(null);

      List<String> headerNames = new ArrayList<>();
      headerNames.add("Authorization");
      when(request.getHeaderNames()).thenReturn(Collections.enumeration(headerNames));
      when(request.getHeader("Authorization")).thenReturn("Bearer token123");

      // action
      String result = loggingAspect.generateCurlCommand(request);

      // assert
      assertThat(result).contains("-H 'Authorization: Bearer token123'");
    }

    @Test
    @DisplayName("验证跳过的请求头不会出现在 curl 命令中")
    void generateCurlCommand_givenSkippedHeaders_shouldNotIncludeSkippedHeaders() {
      // arrange
      when(request.getMethod()).thenReturn("GET");
      when(request.getScheme()).thenReturn("http");
      when(request.getServerName()).thenReturn("localhost");
      when(request.getServerPort()).thenReturn(8080);
      when(request.getRequestURI()).thenReturn("/api/users");
      when(request.getQueryString()).thenReturn(null);

      List<String> headerNames = new ArrayList<>();
      headerNames.add("Host");
      headerNames.add("Content-Length");
      headerNames.add("Connection");
      headerNames.add("Sec-Fetch-Mode");
      headerNames.add("Upgrade-Insecure-Requests");
      headerNames.add("Accept");
      when(request.getHeaderNames()).thenReturn(Collections.enumeration(headerNames));
      when(request.getHeader("Accept")).thenReturn("application/json");

      // action
      String result = loggingAspect.generateCurlCommand(request);

      // assert
      assertThat(result)
          .contains("-H 'Accept: application/json'")
          .doesNotContain("Host")
          .doesNotContain("Content-Length")
          .doesNotContain("Connection")
          .doesNotContain("Sec-Fetch-Mode")
          .doesNotContain("Upgrade-Insecure-Requests");
    }
  }

  @Nested
  @DisplayName("异常情况测试")
  class ExceptionHandlingTests {

    @Test
    @DisplayName("读取请求体时发生 IOException")
    void generateCurlCommand_givenIOExceptionWhenReadingBody_shouldHandleGracefully()
        throws IOException {
      // arrange
      when(request.getMethod()).thenReturn("POST");
      when(request.getScheme()).thenReturn("http");
      when(request.getServerName()).thenReturn("localhost");
      when(request.getServerPort()).thenReturn(8080);
      when(request.getRequestURI()).thenReturn("/api/users");
      when(request.getQueryString()).thenReturn(null);
      when(request.getContentType()).thenReturn("application/json");
      when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
      when(request.getReader()).thenThrow(new IOException("Reader error"));

      // action
      String result = loggingAspect.generateCurlCommand(request);

      // assert
      assertThat(result)
          .contains("curl -X POST")
          .contains("'http://localhost:8080/api/users'")
          .doesNotContain("-d"); // 应该不包含数据标志，因为读取失败
    }

    @Test
    @DisplayName("请求参数为 null")
    void generateCurlCommand_givenNullRequest_shouldReturnErrorMessage() {
      // action
      String result = loggingAspect.generateCurlCommand(null);

      // assert
      assertThat(result).contains("curl command generation failed:");
    }

    @Test
    @DisplayName("请求方法为 null")
    void generateCurlCommand_givenNullMethod_shouldHandleGracefully() {
      // arrange
      when(request.getMethod()).thenReturn(null);
      when(request.getScheme()).thenReturn("http");
      when(request.getServerName()).thenReturn("localhost");
      when(request.getServerPort()).thenReturn(8080);
      when(request.getRequestURI()).thenReturn("/api/users");
      when(request.getQueryString()).thenReturn(null);
      when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());

      // action
      String result = loggingAspect.generateCurlCommand(request);

      // assert
      assertThat(result).contains("curl -X null");
    }

    @Test
    @DisplayName("服务器信息为 null")
    void generateCurlCommand_givenNullServerInfo_shouldUseDefaults() {
      // arrange
      when(request.getMethod()).thenReturn("GET");
      when(request.getScheme()).thenReturn(null);
      when(request.getServerName()).thenReturn(null);
      when(request.getServerPort()).thenReturn(8080);
      when(request.getRequestURI()).thenReturn("/api/users");
      when(request.getQueryString()).thenReturn(null);
      when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());

      // action
      String result = loggingAspect.generateCurlCommand(request);

      // assert
      assertThat(result).isEqualTo("curl -X GET 'http://localhost:8080/api/users'");
    }
  }

  @Nested
  @DisplayName("边界用例测试")
  class BoundaryTests {

    @Test
    @DisplayName("最小化 GET 请求")
    void generateCurlCommand_givenMinimalGetRequest_shouldGenerateBasicCurl() {
      // arrange
      when(request.getMethod()).thenReturn("GET");
      when(request.getScheme()).thenReturn("http");
      when(request.getServerName()).thenReturn("localhost");
      when(request.getServerPort()).thenReturn(80); // 标准端口
      when(request.getRequestURI()).thenReturn("/");
      when(request.getQueryString()).thenReturn(null);
      when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());

      // action
      String result = loggingAspect.generateCurlCommand(request);

      // assert
      assertThat(result).isEqualTo("curl -X GET 'http://localhost/'");
    }

    @Test
    @DisplayName("复杂查询参数 - 包含特殊字符")
    void generateCurlCommand_givenComplexQueryParams_shouldIncludeAllParams() {
      // arrange
      when(request.getMethod()).thenReturn("GET");
      when(request.getScheme()).thenReturn("http");
      when(request.getServerName()).thenReturn("localhost");
      when(request.getServerPort()).thenReturn(8080);
      when(request.getRequestURI()).thenReturn("/api/search");
      when(request.getQueryString())
          .thenReturn("q=hello%20world&filter=type%3Duser&sort=name%2Casc");
      when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());

      // action
      String result = loggingAspect.generateCurlCommand(request);

      // assert
      assertThat(result)
          .isEqualTo(
              "curl -X GET"
                  + " 'http://localhost:8080/api/search?q=hello%20world&filter=type%3Duser&sort=name%2Casc'");
    }

    @Test
    @DisplayName("DELETE 请求 - 不应包含请求体")
    void generateCurlCommand_givenDeleteRequest_shouldNotIncludeBody() {
      // arrange
      when(request.getMethod()).thenReturn("DELETE");
      when(request.getScheme()).thenReturn("http");
      when(request.getServerName()).thenReturn("localhost");
      when(request.getServerPort()).thenReturn(8080);
      when(request.getRequestURI()).thenReturn("/api/users/1");
      when(request.getQueryString()).thenReturn(null);
      when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());

      // action
      String result = loggingAspect.generateCurlCommand(request);

      // assert
      assertThat(result)
          .contains("curl -X DELETE")
          .contains("'http://localhost:8080/api/users/1'")
          .doesNotContain("-d");
    }

    @Test
    @DisplayName("HTTPS 请求 - 标准端口 443")
    void generateCurlCommand_givenHttpsRequestWithStandardPort_shouldNotIncludePort() {
      // arrange
      when(request.getMethod()).thenReturn("GET");
      when(request.getScheme()).thenReturn("https");
      when(request.getServerName()).thenReturn("api.example.com");
      when(request.getServerPort()).thenReturn(443);
      when(request.getRequestURI()).thenReturn("/api/users");
      when(request.getQueryString()).thenReturn(null);
      when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());

      // action
      String result = loggingAspect.generateCurlCommand(request);

      // assert
      assertThat(result).isEqualTo("curl -X GET 'https://api.example.com/api/users'");
    }

    @Test
    @DisplayName("JSON 请求体为 null")
    void generateCurlCommand_givenPostRequestWithNullJsonBody_shouldNotIncludeDataFlag()
        throws IOException {
      // arrange
      when(request.getMethod()).thenReturn("POST");
      when(request.getScheme()).thenReturn("http");
      when(request.getServerName()).thenReturn("localhost");
      when(request.getServerPort()).thenReturn(8080);
      when(request.getRequestURI()).thenReturn("/api/users");
      when(request.getQueryString()).thenReturn(null);
      when(request.getContentType()).thenReturn("application/json");
      when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
      when(request.getReader()).thenReturn(null);

      // action
      String result = loggingAspect.generateCurlCommand(request);

      // assert
      assertThat(result)
          .contains("curl -X POST")
          .contains("'http://localhost:8080/api/users'")
          .doesNotContain("-d");
    }
  }
}
