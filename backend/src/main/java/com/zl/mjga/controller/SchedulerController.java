package com.zl.mjga.controller;

import com.zl.mjga.dto.PageRequestDto;
import com.zl.mjga.dto.PageResponseDto;
import com.zl.mjga.dto.scheduler.JobKeyDto;
import com.zl.mjga.dto.scheduler.JobTriggerDto;
import com.zl.mjga.dto.scheduler.QueryDto;
import com.zl.mjga.dto.scheduler.TriggerKeyDto;
import com.zl.mjga.service.SchedulerService;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scheduler")
@RequiredArgsConstructor
public class SchedulerController {

  private final SchedulerService schedulerService;

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).READ_SCHEDULER_PERMISSION)")
  @GetMapping("/page-query")
  public PageResponseDto<List<JobTriggerDto>> pageQuery(
      @ModelAttribute PageRequestDto pageRequestDto, @ModelAttribute QueryDto queryDto) {
    return schedulerService.getJobWithTriggerBy(pageRequestDto, queryDto);
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_SCHEDULER_PERMISSION)")
  @PostMapping("/trigger/resume")
  public void resumeTrigger(@RequestBody TriggerKeyDto triggerKey) throws SchedulerException {
    schedulerService.resumeTrigger(new TriggerKey(triggerKey.name(), triggerKey.group()));
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_SCHEDULER_PERMISSION)")
  @PostMapping("/trigger/pause")
  public void pauseTrigger(@RequestBody TriggerKeyDto triggerKey) throws SchedulerException {
    schedulerService.pauseTrigger(new TriggerKey(triggerKey.name(), triggerKey.group()));
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_SCHEDULER_PERMISSION)")
  @PostMapping("/job/trigger")
  public void triggerJob(@RequestBody JobKeyDto jobKeyDto, @RequestParam Long startAt)
      throws SchedulerException {
    schedulerService.triggerJob(new JobKey(jobKeyDto.name(), jobKeyDto.group()), new Date(startAt));
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_SCHEDULER_PERMISSION)")
  @PutMapping("/job/update")
  public void updateJob(@RequestBody TriggerKeyDto triggerKey, @RequestParam String cron)
      throws SchedulerException {
    schedulerService.updateCronTrigger(new TriggerKey(triggerKey.name(), triggerKey.group()), cron);
  }
}
