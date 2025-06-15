package com.zl.mjga.integration.mvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zl.mjga.config.minio.MinIoConfig;
import com.zl.mjga.config.security.HttpFireWallConfig;
import com.zl.mjga.controller.IdentityAccessController;
import com.zl.mjga.dto.PageRequestDto;
import com.zl.mjga.dto.PageResponseDto;
import com.zl.mjga.dto.urp.*;
import com.zl.mjga.repository.PermissionRepository;
import com.zl.mjga.repository.RoleRepository;
import com.zl.mjga.repository.UserRepository;
import com.zl.mjga.service.IdentityAccessService;
import io.minio.MinioClient;
import java.util.List;
import org.jooq.generated.mjga.tables.pojos.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = {IdentityAccessController.class})
@Import({HttpFireWallConfig.class})
class UserRolePermissionMvcTest {

  @MockBean private IdentityAccessService identityAccessService;
  @Autowired private MockMvc mockMvc;
  @MockBean private UserRepository userRepository;
  @MockBean private RoleRepository roleRepository;
  @MockBean private PermissionRepository permissionRepository;
  @MockBean private MinioClient minioClient;
  @MockBean private MinIoConfig minIoConfig;

  @Test
  @WithMockUser
  void currentUser_givenValidHttpRequest_shouldSucceedWith200AndReturnJson() throws Exception {
    String stubUsername = "test_04cb017e1fe6";
    UserRolePermissionDto stubUserRolePermissionDto = new UserRolePermissionDto();
    stubUserRolePermissionDto.setId(1L);
    stubUserRolePermissionDto.setUsername(stubUsername);
    User stubUser = new User();
    stubUser.setId(1L);
    stubUser.setEnable(Boolean.TRUE);
    when(userRepository.fetchOneByUsername(anyString())).thenReturn(stubUser);
    when(identityAccessService.queryUniqueUserWithRolePermission(anyLong()))
        .thenReturn(stubUserRolePermissionDto);
    mockMvc
        .perform(get("/iam/me"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value(stubUsername));
  }

  @Test
  @WithMockUser
  void deleteUser_givenValidHttpRequest_shouldSucceedWith200() throws Exception {
    Long stubUserId = 2L;
    mockMvc
        .perform(
            delete(String.format("/iam/user?userId=%s", stubUserId))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .with(csrf()))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  void upsertUser_givenValidHttpRequest_shouldSucceedWith200() throws Exception {
    UserUpsertDto userUpsertDto = new UserUpsertDto();
    userUpsertDto.setUsername("username");
    userUpsertDto.setPassword("password");
    userUpsertDto.setEnable(true);

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(userUpsertDto);

    mockMvc
        .perform(
            post("/iam/user").contentType(MediaType.APPLICATION_JSON).content(json).with(csrf()))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  void upsertRole_givenValidHttpRequest_shouldSucceedWith200() throws Exception {
    RoleUpsertDto roleUpsertDto = new RoleUpsertDto();
    roleUpsertDto.setCode("roleCode");
    roleUpsertDto.setName("name");

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(roleUpsertDto);

    mockMvc
        .perform(
            post("/iam/role").contentType(MediaType.APPLICATION_JSON).content(json).with(csrf()))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  void deleteRole_givenValidHttpRequest_shouldSucceedWith200() throws Exception {
    Long stubRoleId = 1L;
    mockMvc
        .perform(
            delete(String.format("/iam/role?roleId=%s", stubRoleId))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .with(csrf()))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  void upsertPermission_givenValidHttpRequest_shouldSucceedWith200() throws Exception {
    PermissionUpsertDto permissionUpsertDto = new PermissionUpsertDto();
    permissionUpsertDto.setCode("roleCode");
    permissionUpsertDto.setName("name");

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(permissionUpsertDto);

    mockMvc
        .perform(
            post("/iam/permission")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .with(csrf()))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  void deletePermission_givenValidHttpRequest_shouldSucceedWith200() throws Exception {
    Long permissionId = 1L;
    mockMvc
        .perform(
            delete(String.format("/iam/permission?permissionId=%s", permissionId))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .with(csrf()))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  void pageQueryUser_givenValidHttpRequest_shouldSucceedWith200AndReturnJson() throws Exception {
    String stubUsername = "test_04cb017e1fe6";
    UserRolePermissionDto stubUserRolePermissionDto = new UserRolePermissionDto();
    stubUserRolePermissionDto.setId(1L);
    stubUserRolePermissionDto.setUsername(stubUsername);
    when(identityAccessService.pageQueryUser(
            PageRequestDto.of(1, 5), new UserQueryDto(stubUsername, null, null)))
        .thenReturn(new PageResponseDto<>(1, List.of(stubUserRolePermissionDto)));
    mockMvc
        .perform(
            get(String.format("/iam/users?page=1&size=5&username=%s", stubUsername))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].username").value(stubUsername));
  }

  @Test
  @WithMockUser
  void pageQueryRole_givenValidHttpRequest_shouldSucceedWith200AndReturnJson() throws Exception {
    Long stubUserId = 1L;
    Long stubRoleId = 1L;
    String stubRoleCode = "UZ1Ej9vx5y8L4";
    String stubRoleName = "B90KM9Pw2ZH9P8OAS";
    RoleQueryDto stubRoleQueryDto = new RoleQueryDto();
    stubRoleQueryDto.setUserId(stubUserId);
    stubRoleQueryDto.setRoleId(stubRoleId);
    stubRoleQueryDto.setRoleCode(stubRoleCode);
    stubRoleQueryDto.setRoleName(stubRoleName);
    RoleDto stubRoleDto = new RoleDto();
    stubRoleDto.setId(1L);
    stubRoleDto.setName(stubRoleName);
    stubRoleDto.setCode(stubRoleCode);
    stubRoleDto.setPermissions(
        List.of(new PermissionRespDto(1L, "9VWU1nmU89zEVH", "9VWU1nmU89zEVH", false)));
    when(identityAccessService.pageQueryRole(PageRequestDto.of(1, 5), stubRoleQueryDto))
        .thenReturn(new PageResponseDto<>(1, List.of(stubRoleDto)));

    mockMvc
        .perform(
            get(String.format(
                    "/iam/roles?page=1&size=5&userId=%s&roleId=%s&roleCode=%s&roleName=%s",
                    stubUserId, stubRoleId, stubRoleCode, stubRoleName))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].name").value(stubRoleName));
  }

  @Test
  @WithMockUser
  void pageQueryPermission_givenValidHttpRequest_shouldSucceedWith200AndReturnJson()
      throws Exception {
    Long stubRoleId = 1L;
    Long stubPermissionId = 1L;
    String stubPermissionCode = "UZ1Ej9vx5y8L4";
    String stubPermissionName = "B90KM9Pw2ZH9P8OAS";
    PermissionQueryDto stubPermissionQueryDto = new PermissionQueryDto();
    stubPermissionQueryDto.setRoleId(stubRoleId);
    stubPermissionQueryDto.setPermissionId(stubPermissionId);
    stubPermissionQueryDto.setPermissionCode(stubPermissionCode);
    stubPermissionQueryDto.setPermissionName(stubPermissionName);

    PermissionRespDto stubPermissionRespDto = new PermissionRespDto();
    stubPermissionRespDto.setId(stubPermissionId);
    stubPermissionRespDto.setName(stubPermissionName);
    stubPermissionRespDto.setCode(stubPermissionCode);
    when(identityAccessService.pageQueryPermission(PageRequestDto.of(1, 5), stubPermissionQueryDto))
        .thenReturn(new PageResponseDto<>(1, List.of(stubPermissionRespDto)));

    mockMvc
        .perform(
            get(String.format(
                    "/iam/permissions?page=1&size=5&roleId=%s&permissionId=%s&permissionCode=%s&permissionName=%s",
                    stubRoleId, stubPermissionId, stubPermissionCode, stubPermissionName))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].name").value(stubPermissionName));
  }
}
