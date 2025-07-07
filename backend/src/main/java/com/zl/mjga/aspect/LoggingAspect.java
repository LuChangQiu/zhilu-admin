package com.zl.mjga.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zl.mjga.annotation.SkipAopLog;
import com.zl.mjga.repository.UserRepository;
import com.zl.mjga.service.AopLogService;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jooq.generated.mjga.tables.pojos.AopLog;
import org.jooq.generated.mjga.tables.pojos.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
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

  @Around("execution(* com.zl.mjga.service..*(..))")
  public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
    AopLog aopLog = new AopLog();
    return processWithLogging(joinPoint, aopLog);
  }

  @Around("execution(* com.zl.mjga.repository..*(..))")
  public Object logRepository(ProceedingJoinPoint joinPoint) throws Throwable {
    AopLog aopLog = new AopLog();
    return processWithLogging(joinPoint, aopLog);
  }

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
    String username = (String) authentication.getPrincipal();
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
}
