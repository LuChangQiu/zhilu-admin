package com.zl.mjga.service;

import com.zl.mjga.dto.PageRequestDto;
import com.zl.mjga.dto.PageResponseDto;
import com.zl.mjga.dto.aoplog.AopLogQueryDto;
import com.zl.mjga.dto.aoplog.AopLogRespDto;
import com.zl.mjga.repository.AopLogRepository;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.generated.mjga.tables.pojos.AopLog;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AopLogService {

  private final AopLogRepository aopLogRepository;

  @Async
  public void saveLogAsync(AopLog aopLog) {
    try {
      aopLogRepository.insert(aopLog);
    } catch (Exception e) {
      log.error("Failed to save AOP log asynchronously", e);
    }
  }

  public PageResponseDto<List<AopLogRespDto>> pageQueryAopLogs(
      PageRequestDto pageRequestDto, AopLogQueryDto queryDto) {
    Result<Record> records = aopLogRepository.pageFetchBy(pageRequestDto, queryDto);

    if (records.isEmpty()) {
      return PageResponseDto.empty();
    }

    List<AopLogRespDto> aopLogs = records.map((record -> record.into(AopLogRespDto.class)));
    Long totalCount = records.get(0).getValue("total_count", Long.class);

    return new PageResponseDto<>(totalCount, aopLogs);
  }

  public AopLogRespDto getAopLogById(Long id) {
    AopLogQueryDto queryDto = new AopLogQueryDto();
    queryDto.setId(id);
    SelectConditionStep<Record> selectStep = aopLogRepository.selectBy(queryDto);
    return selectStep.fetchOneInto(AopLogRespDto.class);
  }

  @Transactional
  public int deleteLogsBeforeTime(OffsetDateTime beforeTime) {
    return aopLogRepository.deleteBeforeTime(beforeTime);
  }
}
