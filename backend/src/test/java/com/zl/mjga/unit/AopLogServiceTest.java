package com.zl.mjga.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.zl.mjga.dto.PageRequestDto;
import com.zl.mjga.dto.PageResponseDto;
import com.zl.mjga.dto.aoplog.AopLogQueryDto;
import com.zl.mjga.dto.aoplog.AopLogRespDto;
import com.zl.mjga.repository.AopLogRepository;
import com.zl.mjga.service.AopLogService;
import java.time.OffsetDateTime;
import java.util.List;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.generated.mjga.tables.pojos.AopLog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AopLogServiceTest {

  @Mock private AopLogRepository aopLogRepository;

  @Mock private Result<Record> mockResult;

  @Mock private Record mockRecord;

  @InjectMocks private AopLogService aopLogService;

  @Test
  void saveLogAsync_givenValidAopLog_shouldCallRepositoryInsert() {
    // arrange
    AopLog aopLog = createTestAopLog();

    // action
    aopLogService.saveLogAsync(aopLog);

    // assert
    verify(aopLogRepository, times(1)).insert(aopLog);
  }

  @Test
  void pageQueryAopLogs_givenValidRequest_shouldReturnPageResponse() {
    // arrange
    PageRequestDto pageRequestDto = PageRequestDto.of(1, 10);
    AopLogQueryDto queryDto = new AopLogQueryDto();

    when(aopLogRepository.pageFetchBy(pageRequestDto, queryDto)).thenReturn(mockResult);
    when(mockResult.isEmpty()).thenReturn(false);
    when(mockResult.map(any())).thenReturn(List.of(createTestAopLogRespDto()));
    when(mockResult.get(0)).thenReturn(mockRecord);
    when(mockRecord.getValue("total_count", Long.class)).thenReturn(1L);

    // action
    PageResponseDto<List<AopLogRespDto>> result =
        aopLogService.pageQueryAopLogs(pageRequestDto, queryDto);

    // assert
    assertThat(result).isNotNull();
    assertThat(result.getTotal()).isEqualTo(1L);
    assertThat(result.getData()).hasSize(1);
    verify(aopLogRepository, times(1)).pageFetchBy(pageRequestDto, queryDto);
  }

  @Test
  void pageQueryAopLogs_givenEmptyResult_shouldReturnEmptyPage() {
    // arrange
    PageRequestDto pageRequestDto = PageRequestDto.of(1, 10);
    AopLogQueryDto queryDto = new AopLogQueryDto();

    when(aopLogRepository.pageFetchBy(pageRequestDto, queryDto)).thenReturn(mockResult);
    when(mockResult.isEmpty()).thenReturn(true);

    // action
    PageResponseDto<List<AopLogRespDto>> result =
        aopLogService.pageQueryAopLogs(pageRequestDto, queryDto);

    // assert
    assertThat(result).isNotNull();
    assertThat(result.getTotal()).isEqualTo(0L);
    assertThat(result.getData()).isNull();
  }

  @Test
  void deleteLogsBeforeTime_givenValidTime_shouldReturnDeletedCount() {
    // arrange
    OffsetDateTime beforeTime = OffsetDateTime.now().minusDays(30);
    when(aopLogRepository.deleteBeforeTime(beforeTime)).thenReturn(10);

    // action
    int result = aopLogService.deleteLogsBeforeTime(beforeTime);

    // assert
    assertThat(result).isEqualTo(10);
    verify(aopLogRepository, times(1)).deleteBeforeTime(beforeTime);
  }

  private AopLog createTestAopLog() {
    AopLog aopLog = new AopLog();
    aopLog.setClassName("TestController");
    aopLog.setMethodName("testMethod");
    aopLog.setMethodArgs("[\"arg1\"]");
    aopLog.setReturnValue("\"result\"");
    aopLog.setExecutionTime(100L);
    aopLog.setSuccess(true);
    aopLog.setUserId(1L);
    aopLog.setIpAddress("127.0.0.1");
    aopLog.setUserAgent("Test Agent");
    aopLog.setCreateTime(OffsetDateTime.now());
    return aopLog;
  }

  private AopLogRespDto createTestAopLogRespDto() {
    return AopLogRespDto.builder()
        .id(1L)
        .className("TestController")
        .methodName("testMethod")
        .methodArgs("[\"arg1\"]")
        .returnValue("\"result\"")
        .executionTime(100L)
        .success(true)
        .userId(1L)
        .username("testUser")
        .ipAddress("127.0.0.1")
        .userAgent("Test Agent")
        .createTime(OffsetDateTime.now())
        .build();
  }
}
