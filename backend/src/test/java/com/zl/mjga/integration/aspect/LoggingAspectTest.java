package com.zl.mjga.integration.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zl.mjga.annotation.SkipAopLog;
import com.zl.mjga.aspect.LoggingAspect;
import com.zl.mjga.repository.UserRepository;
import com.zl.mjga.service.AopLogService;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.jooq.generated.mjga.tables.pojos.AopLog;
import org.jooq.generated.mjga.tables.pojos.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoggingAspectTest {

  @Mock private AopLogService aopLogService;
  @Mock private ObjectMapper objectMapper;
  @Mock private UserRepository userRepository;
  @Mock private ProceedingJoinPoint joinPoint;
  @Mock private MethodSignature methodSignature;
  @Mock private SecurityContext securityContext;
  @Mock private Authentication authentication;
  @Mock private ServletRequestAttributes servletRequestAttributes;
  @Mock private HttpServletRequest httpServletRequest;

  private LoggingAspect loggingAspect;

  @BeforeEach
  void setUp() {
    loggingAspect = new LoggingAspect(aopLogService, objectMapper, userRepository);
    SecurityContextHolder.setContext(securityContext);
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void logController_givenSuccessfulExecution_shouldSaveSuccessLog() throws Throwable {
    // arrange
    TestController target = new TestController();
    Object[] args = {"arg1", "arg2"};
    String expectedResult = "success";
    User mockUser = createMockUser(123L, "testUser");

    setupAuthenticatedUser("testUser", mockUser);
    setupJoinPoint(target, "testMethod", args, expectedResult);
    setupSerialization("[\"arg1\",\"arg2\"]", "\"success\"");

    try (MockedStatic<RequestContextHolder> mockedRequestContextHolder =
        setupRequestContext("192.168.1.1", "Test-Agent")) {
      // action
      Object result = loggingAspect.logController(joinPoint);

      // assert
      assertThat(result).isEqualTo(expectedResult);
      verifyLogSaved(
          log -> {
            assertThat(log.getClassName()).isEqualTo("TestController");
            assertThat(log.getMethodName()).isEqualTo("testMethod");
            assertThat(log.getMethodArgs()).isEqualTo("[\"arg1\",\"arg2\"]");
            assertThat(log.getReturnValue()).isEqualTo("\"success\"");
            assertThat(log.getSuccess()).isTrue();
            assertThat(log.getUserId()).isEqualTo(123L);
            assertThat(log.getIpAddress()).isEqualTo("192.168.1.1");
            assertThat(log.getUserAgent()).isEqualTo("Test-Agent");
            assertThat(log.getExecutionTime()).isGreaterThanOrEqualTo(0L);
          });
    }
  }

  @Test
  void logController_givenFailedExecution_shouldSaveFailLog() throws Throwable {
    // arrange
    TestController target = new TestController();
    Object[] args = {"arg1"};
    RuntimeException exception = new RuntimeException("Test error");
    User mockUser = createMockUser(123L, "testUser");

    setupAuthenticatedUser("testUser", mockUser);
    setupJoinPoint(target, "failMethod", args, exception);
    setupSerialization("[\"arg1\"]", null);

    try (MockedStatic<RequestContextHolder> mockedRequestContextHolder =
        setupRequestContext("192.168.1.1", "Test-Agent")) {
      // action & assert
      assertThatThrownBy(() -> loggingAspect.logController(joinPoint))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Test error");

      verifyLogSaved(
          log -> {
            assertThat(log.getClassName()).isEqualTo("TestController");
            assertThat(log.getMethodName()).isEqualTo("failMethod");
            assertThat(log.getSuccess()).isFalse();
            assertThat(log.getErrorMessage()).isEqualTo("Test error");
            assertThat(log.getReturnValue()).isNull();
            assertThat(log.getUserId()).isEqualTo(123L);
          });
    }
  }

  @Test
  void logService_givenSuccessfulExecution_shouldSaveSuccessLogWithoutRequestInfo()
      throws Throwable {
    // arrange
    TestService target = new TestService();
    Object[] args = {"serviceArg"};
    String expectedResult = "serviceResult";
    User mockUser = createMockUser(123L, "testUser");

    setupAuthenticatedUser("testUser", mockUser);
    setupJoinPoint(target, "serviceMethod", args, expectedResult);
    setupSerialization("[\"serviceArg\"]", "\"serviceResult\"");

    // action
    Object result = loggingAspect.logService(joinPoint);

    // assert
    assertThat(result).isEqualTo(expectedResult);
    verifyLogSaved(
        log -> {
          assertThat(log.getClassName()).isEqualTo("TestService");
          assertThat(log.getMethodName()).isEqualTo("serviceMethod");
          assertThat(log.getSuccess()).isTrue();
          assertThat(log.getUserId()).isEqualTo(123L);
          // Service层不应该有请求信息
          assertThat(log.getIpAddress()).isNull();
          assertThat(log.getUserAgent()).isNull();
        });
  }

  @Test
  void logRepository_givenSuccessfulExecution_shouldSaveSuccessLogWithoutRequestInfo()
      throws Throwable {
    // arrange
    TestRepository target = new TestRepository();
    Object[] args = {1L};
    Object expectedResult = new Object();
    User mockUser = createMockUser(123L, "testUser");

    setupAuthenticatedUser("testUser", mockUser);
    setupJoinPoint(target, "findById", args, expectedResult);
    setupSerialization("[1]", "{}");

    // action
    Object result = loggingAspect.logRepository(joinPoint);

    // assert
    assertThat(result).isEqualTo(expectedResult);
    verifyLogSaved(
        log -> {
          assertThat(log.getClassName()).isEqualTo("TestRepository");
          assertThat(log.getMethodName()).isEqualTo("findById");
          assertThat(log.getSuccess()).isTrue();
          assertThat(log.getUserId()).isEqualTo(123L);
          // Repository层不应该有请求信息
          assertThat(log.getIpAddress()).isNull();
          assertThat(log.getUserAgent()).isNull();
        });
  }

  @Test
  void logController_givenUnauthenticatedUser_shouldNotLog() throws Throwable {
    // arrange
    TestController target = new TestController();
    String expectedResult = "success";
    Method testMethod = TestController.class.getMethod("testMethod");

    when(joinPoint.getTarget()).thenReturn(target);
    when(joinPoint.proceed()).thenReturn(expectedResult);
    when(joinPoint.getSignature()).thenReturn(methodSignature);
    when(methodSignature.getMethod()).thenReturn(testMethod);

    // Mock SecurityContextHolder to return null authentication
    when(securityContext.getAuthentication()).thenReturn(null);

    try (MockedStatic<RequestContextHolder> mockedRequestContextHolder =
        mockStatic(RequestContextHolder.class)) {
      mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(null);

      // action
      Object result = loggingAspect.logController(joinPoint);

      // assert
      assertThat(result).isEqualTo(expectedResult);
      verify(aopLogService, never()).saveLogAsync(any());
    }
  }

  @Test
  void logController_givenAnonymousUser_shouldNotLog() throws Throwable {
    // arrange
    TestController target = new TestController();
    String expectedResult = "success";
    Method testMethod = TestController.class.getMethod("testMethod");

    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getName()).thenReturn("anonymousUser");
    when(joinPoint.getTarget()).thenReturn(target);
    when(joinPoint.proceed()).thenReturn(expectedResult);
    when(joinPoint.getSignature()).thenReturn(methodSignature);
    when(methodSignature.getMethod()).thenReturn(testMethod);

    // Mock SecurityContextHolder to return anonymous authentication
    when(securityContext.getAuthentication()).thenReturn(authentication);

    try (MockedStatic<RequestContextHolder> mockedRequestContextHolder =
        mockStatic(RequestContextHolder.class)) {
      mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(null);

      // action
      Object result = loggingAspect.logController(joinPoint);

      // assert
      assertThat(result).isEqualTo(expectedResult);
      verify(aopLogService, never()).saveLogAsync(any());
    }
  }

  @Test
  void logController_givenSkipAopLogAnnotation_shouldNotLog() throws Throwable {
    // arrange
    TestController target = new TestController();
    String expectedResult = "success";

    when(joinPoint.getTarget()).thenReturn(target);
    when(joinPoint.getSignature()).thenReturn(methodSignature);
    when(methodSignature.getMethod()).thenReturn(getSkipLogMethod());
    when(joinPoint.proceed()).thenReturn(expectedResult);

    // action
    Object result = loggingAspect.logController(joinPoint);

    // assert
    assertThat(result).isEqualTo(expectedResult);
    verify(aopLogService, never()).saveLogAsync(any());
  }

  @Test
  void logController_givenNullArgs_shouldHandleGracefully() throws Throwable {
    // arrange
    TestController target = new TestController();
    User mockUser = createMockUser(123L, "testUser");

    setupAuthenticatedUser("testUser", mockUser);
    setupJoinPoint(target, "noArgsMethod", null, "result");
    setupSerialization(null, "\"result\"");

    try (MockedStatic<RequestContextHolder> mockedRequestContextHolder =
        setupRequestContext("127.0.0.1", "Test-Agent")) {
      // action
      Object result = loggingAspect.logController(joinPoint);

      // assert
      assertThat(result).isEqualTo("result");
      verifyLogSaved(
          log -> {
            assertThat(log.getMethodArgs()).isNull();
            assertThat(log.getSuccess()).isTrue();
          });
    }
  }

  @Test
  void logController_givenEmptyArgs_shouldHandleGracefully() throws Throwable {
    // arrange
    TestController target = new TestController();
    Object[] emptyArgs = {};
    User mockUser = createMockUser(123L, "testUser");

    setupAuthenticatedUser("testUser", mockUser);
    setupJoinPoint(target, "noArgsMethod", emptyArgs, "result");
    setupSerialization(null, "\"result\"");

    try (MockedStatic<RequestContextHolder> mockedRequestContextHolder =
        setupRequestContext("127.0.0.1", "Test-Agent")) {
      // action
      Object result = loggingAspect.logController(joinPoint);

      // assert
      assertThat(result).isEqualTo("result");
      verifyLogSaved(
          log -> {
            assertThat(log.getMethodArgs()).isNull();
            assertThat(log.getSuccess()).isTrue();
          });
    }
  }

  @Test
  void logController_givenSerializationError_shouldHandleGracefully() throws Throwable {
    // arrange
    TestController target = new TestController();
    Object[] args = {"arg1"};
    String expectedResult = "success";
    User mockUser = createMockUser(123L, "testUser");

    setupAuthenticatedUser("testUser", mockUser);
    setupJoinPoint(target, "testMethod", args, expectedResult);

    // Mock serialization error
    when(objectMapper.writeValueAsString(args))
        .thenThrow(new JsonProcessingException("Serialization failed") {});
    when(objectMapper.writeValueAsString(expectedResult)).thenReturn("\"success\"");

    try (MockedStatic<RequestContextHolder> mockedRequestContextHolder =
        setupRequestContext("127.0.0.1", "Test-Agent")) {
      // action
      Object result = loggingAspect.logController(joinPoint);

      // assert
      assertThat(result).isEqualTo(expectedResult);
      verifyLogSaved(
          log -> {
            assertThat(log.getMethodArgs()).isEqualTo("Serialization failed");
            assertThat(log.getSuccess()).isTrue();
          });
    }
  }

  // Helper methods
  private User createMockUser(Long id, String username) {
    User user = new User();
    user.setId(id);
    user.setUsername(username);
    return user;
  }

  private void setupAuthenticatedUser(String username, User user) {
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getName()).thenReturn(username);
    when(authentication.getPrincipal()).thenReturn(username);
    when(userRepository.fetchOneByUsername(username)).thenReturn(user);
  }

  private void setupJoinPoint(Object target, String methodName, Object[] args, Object result)
      throws Throwable {
    when(joinPoint.getTarget()).thenReturn(target);
    when(joinPoint.getSignature()).thenReturn(methodSignature);
    when(methodSignature.getName()).thenReturn(methodName);
    when(methodSignature.getMethod()).thenReturn(getTestMethod());
    when(joinPoint.getArgs()).thenReturn(args);

    if (result instanceof Throwable) {
      when(joinPoint.proceed()).thenThrow((Throwable) result);
    } else {
      when(joinPoint.proceed()).thenReturn(result);
    }
  }

  private void setupSerialization(String argsJson, String resultJson)
      throws JsonProcessingException {
    if (argsJson != null) {
      when(objectMapper.writeValueAsString(any(Object[].class))).thenReturn(argsJson);
    }
    if (resultJson != null) {
      when(objectMapper.writeValueAsString(argThat(arg -> !(arg instanceof Object[]))))
          .thenReturn(resultJson);
    }
  }

  private MockedStatic<RequestContextHolder> setupRequestContext(
      String ipAddress, String userAgent) {
    MockedStatic<RequestContextHolder> mockedRequestContextHolder =
        mockStatic(RequestContextHolder.class);
    mockedRequestContextHolder
        .when(RequestContextHolder::getRequestAttributes)
        .thenReturn(servletRequestAttributes);
    when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
    when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn(ipAddress);
    when(httpServletRequest.getHeader("User-Agent")).thenReturn(userAgent);
    when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
    return mockedRequestContextHolder;
  }

  private void verifyLogSaved(java.util.function.Consumer<AopLog> logVerifier) {
    ArgumentCaptor<AopLog> logCaptor = ArgumentCaptor.forClass(AopLog.class);
    verify(aopLogService, times(1)).saveLogAsync(logCaptor.capture());
    logVerifier.accept(logCaptor.getValue());
  }

  private java.lang.reflect.Method getTestMethod() throws NoSuchMethodException {
    return TestController.class.getMethod("testMethod");
  }

  private java.lang.reflect.Method getSkipLogMethod() throws NoSuchMethodException {
    return TestController.class.getMethod("skipLogMethod");
  }

  // Test classes for mocking
  private static class TestController {
    public String testMethod() {
      return "test";
    }

    @SkipAopLog(reason = "测试跳过日志记录")
    public String skipLogMethod() {
      return "test";
    }
  }

  private static class TestService {
    public String testMethod() {
      return "test";
    }
  }

  private static class TestRepository {
    public String testMethod() {
      return "test";
    }
  }
}
