package com.zl.mjga.integration.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.zl.mjga.dto.PageRequestDto;
import com.zl.mjga.dto.aoplog.AopLogQueryDto;
import com.zl.mjga.repository.AopLogRepository;
import java.time.OffsetDateTime;
import java.util.List;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.generated.mjga.tables.pojos.AopLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

public class AopLogRepositoryTest extends AbstractDataAccessLayerTest {

  @Autowired private AopLogRepository aopLogRepository;

  @Test
  @Sql(
      statements = {
        "INSERT INTO mjga.user (id, username, password) VALUES (1, 'testUser', 'password')",
        "INSERT INTO mjga.aop_log (id, class_name, method_name, method_args, return_value,"
            + " execution_time, success, user_id, ip_address) VALUES (1, 'TestController',"
            + " 'testMethod', '[\"arg1\"]', '\"result\"', 100, true, 1, '127.0.0.1')",
        "INSERT INTO mjga.aop_log (id, class_name, method_name, method_args, return_value,"
            + " execution_time, success, error_message) VALUES (2, 'TestService', 'failMethod',"
            + " '[\"arg2\"]', null, 200, false, 'Test error')",
        "INSERT INTO mjga.aop_log (id, class_name, method_name, execution_time, success) VALUES (3,"
            + " 'TestRepository', 'queryMethod', 50, true)"
      })
  void pageFetchBy_givenValidQuery_shouldReturnCorrectResults() {
    // arrange
    AopLogQueryDto queryDto = new AopLogQueryDto();
    queryDto.setClassName("Test");
    PageRequestDto pageRequestDto = PageRequestDto.of(1, 10);

    // action
    Result<Record> result = aopLogRepository.pageFetchBy(pageRequestDto, queryDto);

    // assert
    assertThat(result).hasSize(3);
    assertThat(result.get(0).getValue("total_count", Long.class)).isEqualTo(3L);
  }

  @Test
  @Sql(
      statements = {
        "INSERT INTO mjga.aop_log (id, class_name, method_name, execution_time, success) VALUES (1,"
            + " 'TestController', 'method1', 100, true)",
        "INSERT INTO mjga.aop_log (id, class_name, method_name, execution_time, success) VALUES (2,"
            + " 'TestService', 'method2', 200, false)",
        "INSERT INTO mjga.aop_log (id, class_name, method_name, execution_time, success) VALUES (3,"
            + " 'TestRepository', 'method3', 50, true)"
      })
  void fetchBy_givenClassNameQuery_shouldReturnFilteredResults() {
    // arrange
    AopLogQueryDto queryDto = new AopLogQueryDto();
    queryDto.setClassName("TestController");

    // action
    List<AopLog> result = aopLogRepository.fetchBy(queryDto);

    // assert
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getClassName()).isEqualTo("TestController");
    assertThat(result.get(0).getMethodName()).isEqualTo("method1");
  }

  @Test
  @Sql(
      statements = {
        "INSERT INTO mjga.aop_log (id, class_name, method_name, execution_time, success) VALUES (1,"
            + " 'TestController', 'method1', 100, true)",
        "INSERT INTO mjga.aop_log (id, class_name, method_name, execution_time, success) VALUES (2,"
            + " 'TestService', 'method2', 200, false)"
      })
  void fetchBy_givenSuccessQuery_shouldReturnOnlySuccessfulLogs() {
    // arrange
    AopLogQueryDto queryDto = new AopLogQueryDto();
    queryDto.setSuccess(true);

    // action
    List<AopLog> result = aopLogRepository.fetchBy(queryDto);

    // assert
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getSuccess()).isTrue();
  }

  @Test
  @Sql(
      statements = {
        "INSERT INTO mjga.aop_log (id, class_name, method_name, execution_time, success) VALUES (1,"
            + " 'TestController', 'method1', 50, true)",
        "INSERT INTO mjga.aop_log (id, class_name, method_name, execution_time, success) VALUES (2,"
            + " 'TestService', 'method2', 150, false)",
        "INSERT INTO mjga.aop_log (id, class_name, method_name, execution_time, success) VALUES (3,"
            + " 'TestRepository', 'method3', 250, true)"
      })
  void fetchBy_givenExecutionTimeRange_shouldReturnFilteredResults() {
    // arrange
    AopLogQueryDto queryDto = new AopLogQueryDto();
    queryDto.setMinExecutionTime(100L);
    queryDto.setMaxExecutionTime(200L);

    // action
    List<AopLog> result = aopLogRepository.fetchBy(queryDto);

    // assert
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getExecutionTime()).isEqualTo(150L);
  }

  @Test
  @Sql(
      statements = {
        "INSERT INTO mjga.aop_log (id, class_name, method_name, execution_time, success) VALUES (1,"
            + " 'TestController', 'method1', 100, true)",
        "INSERT INTO mjga.aop_log (id, class_name, method_name, execution_time, success) VALUES (2,"
            + " 'TestService', 'method2', 200, false)",
        "INSERT INTO mjga.aop_log (id, class_name, method_name, execution_time, success) VALUES (3,"
            + " 'TestRepository', 'method3', 50, true)"
      })
  void deleteByIds_givenValidIds_shouldDeleteCorrectRecords() {
    // arrange
    List<Long> idsToDelete = List.of(1L, 3L);

    // action
    int deletedCount = aopLogRepository.deleteByIds(idsToDelete);

    // assert
    assertThat(deletedCount).isEqualTo(2);

    // verify remaining record
    List<AopLog> remaining = aopLogRepository.findAll();
    assertThat(remaining).hasSize(1);
    assertThat(remaining.get(0).getId()).isEqualTo(2L);
  }

  @Test
  @Sql(
      statements = {
        "INSERT INTO mjga.aop_log (id, class_name, method_name, execution_time, success,"
            + " create_time) VALUES (1, 'TestController', 'method1', 100, true, '2023-01-01"
            + " 00:00:00+00')",
        "INSERT INTO mjga.aop_log (id, class_name, method_name, execution_time, success,"
            + " create_time) VALUES (2, 'TestService', 'method2', 200, false, '2023-06-01"
            + " 00:00:00+00')",
        "INSERT INTO mjga.aop_log (id, class_name, method_name, execution_time, success,"
            + " create_time) VALUES (3, 'TestRepository', 'method3', 50, true, '2023-12-01"
            + " 00:00:00+00')"
      })
  void deleteBeforeTime_givenValidTime_shouldDeleteOldRecords() {
    // arrange
    OffsetDateTime cutoffTime = OffsetDateTime.parse("2023-07-01T00:00:00Z");

    // action
    int deletedCount = aopLogRepository.deleteBeforeTime(cutoffTime);

    // assert
    assertThat(deletedCount).isEqualTo(2);

    // verify remaining record
    List<AopLog> remaining = aopLogRepository.findAll();
    assertThat(remaining).hasSize(1);
    assertThat(remaining.get(0).getId()).isEqualTo(3L);
  }

  @Test
  void deleteByIds_givenEmptyList_shouldReturnZero() {
    // arrange
    List<Long> emptyIds = List.of();

    // action
    int deletedCount = aopLogRepository.deleteByIds(emptyIds);

    // assert
    assertThat(deletedCount).isEqualTo(0);
  }

  @Test
  void deleteByIds_givenNullList_shouldReturnZero() {
    // arrange & action
    int deletedCount = aopLogRepository.deleteByIds(null);

    // assert
    assertThat(deletedCount).isEqualTo(0);
  }
}
