package com.zl.mjga.integration.mvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zl.mjga.config.minio.MinIoConfig;
import com.zl.mjga.config.security.HttpFireWallConfig;
import com.zl.mjga.controller.AopLogController;
import com.zl.mjga.dto.PageRequestDto;
import com.zl.mjga.dto.PageResponseDto;
import com.zl.mjga.dto.aoplog.AopLogQueryDto;
import com.zl.mjga.dto.aoplog.AopLogRespDto;
import com.zl.mjga.repository.AopLogRepository;
import com.zl.mjga.repository.PermissionRepository;
import com.zl.mjga.repository.RoleRepository;
import com.zl.mjga.repository.UserRepository;
import com.zl.mjga.service.AopLogService;
import io.minio.MinioClient;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = {AopLogController.class})
@Import({HttpFireWallConfig.class})
public class AopLogControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private AopLogService aopLogService;
  @MockBean private AopLogRepository aopLogRepository;

  @MockBean private UserRepository userRepository;
  @MockBean private RoleRepository roleRepository;
  @MockBean private PermissionRepository permissionRepository;
  @MockBean private MinioClient minioClient;
  @MockBean private MinIoConfig minIoConfig;

  @Test
  @WithMockUser(authorities = "READ_USER_ROLE_PERMISSION")
  void pageQueryAopLogs_givenValidRequest_shouldReturnOk() throws Exception {
    // arrange
    PageResponseDto<List<AopLogRespDto>> mockResponse =
        new PageResponseDto<>(1L, List.of(createTestAopLogRespDto()));
    when(aopLogService.pageQueryAopLogs(any(PageRequestDto.class), any(AopLogQueryDto.class)))
        .thenReturn(mockResponse);

    // action & assert
    mockMvc
        .perform(
            get("/aop-log/page-query")
                .param("page", "1")
                .param("size", "10")
                .param("className", "TestController"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.total").value(1))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].className").value("TestController"));
  }

  @Test
  void pageQueryAopLogs_givenNoAuth_shouldReturnUnauthorized() throws Exception {
    // action & assert
    mockMvc
        .perform(get("/aop-log/page-query").param("page", "1").param("size", "10"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(authorities = "READ_USER_ROLE_PERMISSION")
  void getAopLogById_givenValidId_shouldReturnOk() throws Exception {
    // arrange
    Long id = 1L;
    AopLogRespDto mockResponse = createTestAopLogRespDto();
    when(aopLogService.getAopLogById(id)).thenReturn(mockResponse);

    // action & assert
    mockMvc
        .perform(get("/aop-log/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.className").value("TestController"))
        .andExpect(jsonPath("$.methodName").value("testMethod"));
  }

  @Test
  @WithMockUser(authorities = "READ_USER_ROLE_PERMISSION")
  void getAopLogById_givenNonExistingId_shouldReturnOkWithNull() throws Exception {
    // arrange
    Long id = 999L;
    when(aopLogService.getAopLogById(id)).thenReturn(null);

    // action & assert
    mockMvc
        .perform(get("/aop-log/{id}", id))
        .andExpect(status().isOk())
        .andExpect(content().string(""));
  }

  @Test
  @WithMockUser(authorities = "WRITE_USER_ROLE_PERMISSION")
  void deleteAopLogs_givenValidIds_shouldReturnOk() throws Exception {
    // arrange
    List<Long> ids = List.of(1L, 2L, 3L);
    when(aopLogRepository.deleteByIds(ids)).thenReturn(3);

    // action & assert
    mockMvc
        .perform(
            delete("/aop-log/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids))
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(content().string("3"));
  }

  @Test
  void deleteAopLogs_givenNoAuth_shouldReturnUnauthorized() throws Exception {
    // arrange
    List<Long> ids = List.of(1L, 2L, 3L);

    // action & assert
    mockMvc
        .perform(
            delete("/aop-log/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids))
                .with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(authorities = "WRITE_USER_ROLE_PERMISSION")
  void deleteAopLog_givenValidId_shouldReturnOk() throws Exception {
    // arrange
    Long id = 1L;
    when(aopLogRepository.deleteByIds(List.of(id))).thenReturn(1);

    // action & assert
    mockMvc.perform(delete("/aop-log/{id}", id).with(csrf())).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = "WRITE_USER_ROLE_PERMISSION")
  void deleteLogsBeforeTime_givenValidTime_shouldReturnOk() throws Exception {
    // arrange
    OffsetDateTime beforeTime = OffsetDateTime.now().minusDays(7);
    when(aopLogService.deleteLogsBeforeTime(beforeTime)).thenReturn(5);

    // action & assert
    mockMvc
        .perform(delete("/aop-log/before").param("beforeTime", beforeTime.toString()).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(content().string("5"));
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
