package com.zl.mjga.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 跳过AOP日志记录注解
 *
 * <p>在方法上添加此注解，该方法将不会被AOP日志切面拦截和记录。
 *
 * <p>使用场景：
 *
 * <ul>
 *   <li>敏感操作方法，不希望记录日志
 *   <li>高频调用方法，避免产生过多日志
 *   <li>内部工具方法，不需要业务日志记录
 * </ul>
 *
 * <p>使用示例：
 *
 * <pre>{@code
 * @SkipAopLog
 * public void sensitiveMethod() {
 *     // 此方法不会被AOP日志记录
 * }
 * }</pre>
 *
 * @author AOP Log System
 * @since 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SkipAopLog {

  /**
   * 跳过日志记录的原因说明（可选）
   *
   * @return 跳过原因
   */
  String reason() default "";
}
