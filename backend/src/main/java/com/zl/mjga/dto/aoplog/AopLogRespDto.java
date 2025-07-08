package com.zl.mjga.dto.aoplog;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AopLogRespDto {

  /** 主键ID */
  private Long id;

  /** 类名 */
  private String className;

  /** 方法名 */
  private String methodName;

  /** 方法参数 */
  private String methodArgs;

  /** 返回值 */
  private String returnValue;

  /** 执行时间（毫秒） */
  private Long executionTime;

  /** 是否成功 */
  private Boolean success;

  /** 错误信息 */
  private String errorMessage;

  /** 用户ID */
  private Long userId;

  /** 用户名 */
  private String username;

  /** IP地址 */
  private String ipAddress;

  /** 用户代理 */
  private String userAgent;

  /** curl命令 */
  private String curl;

  /** 创建时间 */
  private OffsetDateTime createTime;
}
