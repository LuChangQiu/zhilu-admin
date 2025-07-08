package com.zl.mjga.dto.aoplog;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** AOP日志查询DTO */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AopLogQueryDto {

  /** ID */
  private Long id;

  /** 类名 */
  private String className;

  /** 方法名 */
  private String methodName;

  /** 是否成功 */
  private Boolean success;

  /** 用户ID */
  private Long userId;

  /** IP地址 */
  private String ipAddress;

  /** 开始时间 */
  private OffsetDateTime startTime;

  /** 结束时间 */
  private OffsetDateTime endTime;

  /** 最小执行时间（毫秒） */
  private Long minExecutionTime;

  /** 最大执行时间（毫秒） */
  private Long maxExecutionTime;
}
