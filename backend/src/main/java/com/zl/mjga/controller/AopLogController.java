package com.zl.mjga.controller;

import com.zl.mjga.dto.PageRequestDto;
import com.zl.mjga.dto.PageResponseDto;
import com.zl.mjga.dto.aoplog.AopLogQueryDto;
import com.zl.mjga.dto.aoplog.AopLogRespDto;
import com.zl.mjga.repository.AopLogRepository;
import com.zl.mjga.service.AopLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/aop-log")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "AOP日志管理", description = "AOP日志查看和管理接口")
public class AopLogController {

  private final AopLogService aopLogService;
  private final AopLogRepository aopLogRepository;

  @GetMapping("/page-query")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "分页查询AOP日志", description = "支持多种条件筛选的分页查询")
  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).READ_USER_ROLE_PERMISSION)")
  public PageResponseDto<List<AopLogRespDto>> pageQueryAopLogs(
      @ModelAttribute @Valid PageRequestDto pageRequestDto,
      @ModelAttribute AopLogQueryDto queryDto) {
    return aopLogService.pageQueryAopLogs(pageRequestDto, queryDto);
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "查询日志详情", description = "根据ID查询单条日志的详细信息")
  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).READ_USER_ROLE_PERMISSION)")
  public AopLogRespDto getAopLogById(@Parameter(description = "日志ID") @PathVariable Long id) {
    return aopLogService.getAopLogById(id);
  }

  @DeleteMapping("/batch")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "批量删除日志", description = "根据ID列表批量删除日志")
  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_USER_ROLE_PERMISSION)")
  public int deleteAopLogs(@Parameter(description = "日志ID列表") @RequestBody List<Long> ids) {
    return aopLogRepository.deleteByIds(ids);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "删除单条日志", description = "根据ID删除单条日志")
  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_USER_ROLE_PERMISSION)")
  public void deleteAopLog(@Parameter(description = "日志ID") @PathVariable Long id) {
    aopLogRepository.deleteById(id);
  }

  @DeleteMapping("/before")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "删除指定时间前的日志", description = "删除指定时间之前的所有日志")
  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_USER_ROLE_PERMISSION)")
  public int deleteLogsBeforeTime(
      @Parameter(description = "截止时间") @RequestParam OffsetDateTime beforeTime) {
    return aopLogService.deleteLogsBeforeTime(beforeTime);
  }
}
