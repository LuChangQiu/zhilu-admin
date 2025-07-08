package com.zl.mjga.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zl.mjga.annotation.SkipAopLog;
import com.zl.mjga.repository.UserRepository;
import com.zl.mjga.service.AopLogService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Enumeration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jooq.generated.mjga.tables.pojos.AopLog;
import org.jooq.generated.mjga.tables.pojos.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "aop.logging.enabled", havingValue = "true", matchIfMissing = true)
public class LoggingAspect {

  private final AopLogService aopLogService;
  private final ObjectMapper objectMapper;
  private final UserRepository userRepository;

  @Around("execution(* com.zl.mjga.controller..*(..))")
  public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
    AopLog aopLog = new AopLog();
    setRequestInfo(aopLog);
    return processWithLogging(joinPoint, aopLog);
  }

//  @Around("execution(* com.zl.mjga.service..*(..))")
//  public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
//    AopLog aopLog = new AopLog();
//    return processWithLogging(joinPoint, aopLog);
//  }

  private Object processWithLogging(ProceedingJoinPoint joinPoint, AopLog aopLog) throws Throwable {
    if (shouldSkipLogging(joinPoint) || !isUserAuthenticated()) {
      return joinPoint.proceed();
    }
    return logMethodExecution(joinPoint, aopLog);
  }

  private boolean shouldSkipLogging(ProceedingJoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    return method.isAnnotationPresent(SkipAopLog.class);
  }

  private boolean isUserAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null
        && authentication.isAuthenticated()
        && !"anonymousUser".equals(authentication.getName());
  }

  private Long getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    User user = userRepository.fetchOneByUsername(username);
    return user.getId();
  }

  private Object logMethodExecution(ProceedingJoinPoint joinPoint, AopLog aopLog) throws Throwable {
    Instant startTime = Instant.now();
    String className = joinPoint.getTarget().getClass().getSimpleName();
    String methodName = joinPoint.getSignature().getName();

    populateBasicLogInfo(aopLog, className, methodName, joinPoint.getArgs());

    Object result = null;
    Exception executionException = null;

    try {
      result = joinPoint.proceed();
      aopLog.setReturnValue(serializeReturnValue(result));
    } catch (Exception e) {
      executionException = e;
      aopLog.setErrorMessage(e.getMessage());
      log.error("Method execution failed: {}.{}", className, methodName, e);
    } finally {
      aopLog.setExecutionTime(Duration.between(startTime, Instant.now()).toMillis());
      aopLog.setSuccess(executionException == null);
      saveLogSafely(aopLog);
    }

    if (executionException != null) {
      throw executionException;
    }

    return result;
  }

  private void populateBasicLogInfo(
      AopLog aopLog, String className, String methodName, Object[] args) {
    aopLog.setClassName(className);
    aopLog.setMethodName(methodName);
    aopLog.setMethodArgs(serializeArgs(args));
    aopLog.setUserId(getCurrentUserId());
  }

  private void saveLogSafely(AopLog aopLog) {
    try {
      aopLogService.saveLogAsync(aopLog);
    } catch (Exception e) {
      log.error(
          "Failed to save AOP log for {}.{}", aopLog.getClassName(), aopLog.getMethodName(), e);
    }
  }

  private void setRequestInfo(AopLog aopLog) {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes == null) {
      return;
    }

    HttpServletRequest request = attributes.getRequest();
    aopLog.setIpAddress(getClientIpAddress(request));
    aopLog.setUserAgent(request.getHeader("User-Agent"));
    aopLog.setCurl(generateCurlCommand(request));
  }

  private String getClientIpAddress(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null
        && !xForwardedFor.isEmpty()
        && !"unknown".equalsIgnoreCase(xForwardedFor)) {
      return xForwardedFor.split(",")[0].trim();
    }

    String xRealIp = request.getHeader("X-Real-IP");
    if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
      return xRealIp;
    }

    return request.getRemoteAddr();
  }

  private String serializeArgs(Object[] args) {
    if (ArrayUtils.isEmpty(args)) {
      return null;
    } else {
      return serializeObject(args);
    }
  }

  private String serializeReturnValue(Object returnValue) {
    if (returnValue == null) {
      return null;
    } else {
      return serializeObject(returnValue);
    }
  }

  private String serializeObject(Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize {} ", obj, e);
      return e.getMessage();
    }
  }

  public String generateCurlCommand(HttpServletRequest request) {
    try {
      StringBuilder curl = new StringBuilder("curl -X ");

      curl.append(request.getMethod());

      String url = getFullRequestUrl(request);
      curl.append(" '").append(url).append("'");

      appendHeaders(curl, request);

      if (hasRequestBody(request.getMethod())) {
        appendRequestBody(curl, request);
      }
      return curl.toString();
    } catch (Exception e) {
      log.error("Failed to generate curl command", e);
      return "curl command generation failed: " + e.getMessage();
    }
  }

  private String getFullRequestUrl(HttpServletRequest request) {
    StringBuilder url = new StringBuilder();

    String scheme = request.getScheme();
    String serverName = request.getServerName();
    int serverPort = request.getServerPort();

    if (scheme == null) {
      scheme = "http";
    }
    if (serverName == null) {
      serverName = "localhost";
    }

    url.append(scheme).append("://").append(serverName);

    if ((scheme.equals("http") && serverPort != 80)
        || (scheme.equals("https") && serverPort != 443)) {
      url.append(":").append(serverPort);
    }

    url.append(request.getRequestURI());
    if (request.getQueryString() != null) {
      url.append("?").append(request.getQueryString());
    }

    return url.toString();
  }

  private void appendHeaders(StringBuilder curl, HttpServletRequest request) {
    Enumeration<String> headerNames = request.getHeaderNames();
    for (String headerName : Collections.list(headerNames)) {
      if (shouldSkipHeader(headerName)) {
        continue;
      }

      String headerValue = request.getHeader(headerName);
      curl.append(" -H '").append(headerName).append(": ").append(headerValue).append("'");
    }
  }

  private boolean shouldSkipHeader(String headerName) {
    String lowerName = headerName.toLowerCase();
    return lowerName.equals("host")
        || lowerName.equals("content-length")
        || lowerName.equals("connection")
        || lowerName.startsWith("sec-")
        || lowerName.equals("upgrade-insecure-requests");
  }

  private boolean hasRequestBody(String method) {
    return "POST".equalsIgnoreCase(method)
        || "PUT".equalsIgnoreCase(method)
        || "PATCH".equalsIgnoreCase(method);
  }

  private void appendRequestBody(StringBuilder curl, HttpServletRequest request) {
    try {
      String contentType = request.getContentType();
      if (StringUtils.contains(contentType, "application/json")) {
        String body = getRequestBody(request);
        if (StringUtils.isNotEmpty(body)) {
          curl.append(" -d '").append(body.replace("'", "\\'")).append("'");
        }
      } else if (StringUtils.contains(contentType, "application/x-www-form-urlencoded")) {
        appendFormData(curl, request);
      }
    } catch (Exception e) {
      log.warn("Failed to append request body to curl command", e);
    }
  }

  private String getRequestBody(HttpServletRequest request) {
    try (BufferedReader reader = request.getReader()) {
      if (reader == null) {
        return null;
      }
      StringBuilder body = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        body.append(line);
      }
      return body.toString();
    } catch (IOException e) {
      log.warn("Failed to read request body", e);
      return null;
    }
  }

  private void appendFormData(StringBuilder curl, HttpServletRequest request) {
    Enumeration<String> paramNames = request.getParameterNames();
    StringBuilder formData = new StringBuilder();
    while (paramNames.hasMoreElements()) {
      String paramName = paramNames.nextElement();
      String[] paramValues = request.getParameterValues(paramName);
      for (String paramValue : paramValues) {
        if (!formData.isEmpty()) {
          formData.append("&");
        }
        formData.append(paramName).append("=").append(paramValue);
      }
    }
    if (!formData.isEmpty()) {
      curl.append(" -d '").append(formData).append("'");
    }
  }
}
