package com.zl.mjga.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.generated.mjga.tables.Permission.PERMISSION;
import static org.jooq.generated.mjga.tables.Role.ROLE;
import static org.jooq.generated.mjga.tables.User.USER;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

import com.zl.mjga.dto.PageRequestDto;
import com.zl.mjga.dto.PageResponseDto;
import com.zl.mjga.dto.urp.*;
import com.zl.mjga.repository.*;
import com.zl.mjga.service.IdentityAccessService;
import java.sql.SQLException;
import java.util.List;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.generated.mjga.tables.pojos.*;
import org.jooq.generated.mjga.tables.pojos.Role;
import org.jooq.generated.mjga.tables.pojos.User;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserRolePermissionUnitTest {
  @InjectMocks @Spy private IdentityAccessService identityAccessService;

  @Mock private UserRepository userRepository;

  @Mock private RoleRepository roleRepository;
  @Mock private UserRoleMapRepository userRoleMapRepository;
  @Mock private PermissionRepository permissionRepository;
  @Mock private RolePermissionMapRepository rolePermissionMapRepository;
  @Mock private PasswordEncoder passwordEncoder;

  private static DSLContext dslContext;

  private static MockConnection connection;

  @BeforeAll
  static void setUp() {
    MockDataProvider provider = ctx -> new MockResult[0];
    connection = new MockConnection(provider);
    dslContext = DSL.using(connection, SQLDialect.POSTGRES);
  }

  @AfterAll
  static void setDown() throws SQLException {
    connection.close();
  }

  @Test
  void pageQueryUser_selected2UserShouldReturnUserRolePermissionAndTotal() {
    // arrange
    Long stubUserId1 = 1L;
    String stubUserName1 = "yEJVEJBC2j9PGi";
    String stubUserPassword1 = "c21W03p1201jCz";

    Long stubUserId2 = 2L;
    String stubUserName2 = "1jpziB82YUs3Jbh";
    String stubUserPassword2 = "c21W03p1201jCz";

    Long stubRoleId = 1L;
    String stubRoleName = "54X3UYRzx0wiy9";
    String stubRoleCode = "mzxN6WQA3AErI";

    Long stubPermissionId = 1L;
    String stubPermissionName = "BNOz058K9EWE";
    String stubPermissionCode = "BNOz058K9EWE";

    Result<Record> mockResult =
        dslContext.newResult(
            List.of(
                USER.ID,
                USER.USERNAME,
                USER.PASSWORD,
                USER.ENABLE,
                USER.CREATE_TIME,
                DSL.field("total_user", Integer.class)));
    mockResult.add(
        dslContext
            .newRecord(
                USER.ID,
                USER.USERNAME,
                USER.PASSWORD,
                USER.ENABLE,
                USER.CREATE_TIME,
                DSL.field("total_user", Integer.class))
            .values(stubUserId1, stubUserName2, stubUserPassword2, true, null, 2));
    mockResult.add(
        dslContext
            .newRecord(
                USER.ID,
                USER.USERNAME,
                USER.PASSWORD,
                USER.ENABLE,
                USER.CREATE_TIME,
                DSL.field("total_user", Integer.class))
            .values(stubUserId2, stubUserName2, stubUserPassword2, true, null, 2));
    UserRolePermissionDto mockUserRolePermissionDto1 = new UserRolePermissionDto();
    RoleDto mockRoleDto = new RoleDto();
    mockRoleDto.setId(stubRoleId);
    mockRoleDto.setCode(stubRoleCode);
    mockRoleDto.setName(stubRoleName);
    PermissionRespDto permissionRespDto = new PermissionRespDto();
    permissionRespDto.setId(stubPermissionId);
    permissionRespDto.setCode(stubPermissionCode);
    permissionRespDto.setName(stubPermissionName);
    mockRoleDto.getPermissions().add(permissionRespDto);
    mockUserRolePermissionDto1.setId(stubUserId1);
    mockUserRolePermissionDto1.setUsername(stubUserName1);
    mockUserRolePermissionDto1.setPassword(stubUserPassword1);
    mockUserRolePermissionDto1.getRoles().add(mockRoleDto);

    UserRolePermissionDto mockUserRolePermissionDto2 = new UserRolePermissionDto();
    mockUserRolePermissionDto2.setId(stubUserId2);
    mockUserRolePermissionDto2.setUsername(stubUserName2);
    mockUserRolePermissionDto2.setPassword(stubUserPassword2);

    doReturn(mockUserRolePermissionDto1)
        .when(identityAccessService)
        .queryUniqueUserWithRolePermission(stubUserId1);
    doReturn(mockUserRolePermissionDto2)
        .when(identityAccessService)
        .queryUniqueUserWithRolePermission(stubUserId2);
    when(userRepository.pageFetchBy(any(PageRequestDto.class), any(UserQueryDto.class)))
        .thenReturn(mockResult);

    // action
    PageResponseDto<List<UserRolePermissionDto>> result =
        identityAccessService.pageQueryUser(
            PageRequestDto.of(1, 10), new UserQueryDto(stubUserName2));

    // assert
    List<UserRolePermissionDto> userRolePermissionDtoList = result.getData();
    assertThat(result.getTotal()).isEqualTo(2L);
    assertThat(userRolePermissionDtoList.size()).isEqualTo(2L);
    assertThat(userRolePermissionDtoList.get(0).getRoles().size()).isEqualTo(1L);
    assertThat(userRolePermissionDtoList.get(1).getRoles().size()).isEqualTo(0L);
    assertThat(userRolePermissionDtoList.get(1).getUsername()).isEqualTo(stubUserName2);
    assertThat(userRolePermissionDtoList.get(0).getRoles().get(0).getName())
        .isEqualTo(stubRoleName);
    assertThat(userRolePermissionDtoList.get(0).getRoles().get(0).getPermissions().get(0).getCode())
        .isEqualTo(stubPermissionCode);
  }

  @Test
  void queryUser_selected0Row_shouldReturnEmptyListWithPage() {
    Result<Record> mockResult =
        dslContext.newResult(
            List.of(
                USER.ID,
                USER.USERNAME,
                USER.PASSWORD,
                USER.ENABLE,
                USER.CREATE_TIME,
                DSL.field("total_user", Integer.class)));
    when(userRepository.pageFetchBy(any(PageRequestDto.class), any(UserQueryDto.class)))
        .thenReturn(mockResult);
    PageResponseDto<List<UserRolePermissionDto>> result =
        identityAccessService.pageQueryUser(
            PageRequestDto.of(1, 10), new UserQueryDto("agydCO1Yi99a"));
    assertThat(result.getTotal()).isEqualTo(0);
    assertThat(result.getData()).isNull();
  }

  @Test
  void
      queryUniqueUserWithRolePermission_whenUserHasBeenFound_shouldReturnUniqueUserRolePermissionDto() {
    Long stubUserId = 1L;
    String stubUserName = "yEJVEJBC2j9PGi";
    String stubUserPassword = "c21W03p1201jCz";
    Long stubRoleId = 1L;
    String stubRoleName = "G5N6Xkjg0i9UC4Vltv";
    String stubRoleCode = "G5N6Xkjg0i9UC4Vltv";

    Long stubPermissionId = 1L;
    String stubPermissionName = "BNOz058K9EWE";
    String stubPermissionCode = "BNOz058K9EWE";

    Long stubPermissionId2 = 2L;
    String stubPermissionName2 = "u6igc4BctOm1ON6X";
    String stubPermissionCode2 = "u6igc4BctOm1ON6X";

    UserRolePermissionDto mockResult = new UserRolePermissionDto();
    mockResult.setUsername(stubUserName);
    mockResult.setPassword(stubUserPassword);
    mockResult.setId(stubRoleId);
    mockResult.setRoles(
        List.of(
            new RoleDto(
                stubRoleId,
                stubRoleName,
                stubRoleCode,
                true,
                List.of(
                    new PermissionRespDto(
                        stubPermissionId, stubPermissionName, stubPermissionCode, false),
                    new PermissionRespDto(
                        stubPermissionId2, stubPermissionName2, stubPermissionCode2, false)))));

    when(userRepository.fetchUniqueUserDtoWithNestedRolePermissionBy(stubUserId))
        .thenReturn(mockResult);
    UserRolePermissionDto userRolePermissionDto =
        identityAccessService.queryUniqueUserWithRolePermission(stubUserId);
    assertThat(userRolePermissionDto).isNotNull();
    assertThat(userRolePermissionDto.getRoles().size()).isEqualTo(1L);
    assertThat(userRolePermissionDto.getRoles().get(0).getPermissions().size()).isEqualTo(2L);
    assertThat(userRolePermissionDto.getUsername()).isEqualTo(stubUserName);
    assertThat(userRolePermissionDto.getRoles().get(0).getPermissions().get(0).getName())
        .isEqualTo(stubPermissionName);
    assertThat(userRolePermissionDto.getRoles().get(0).getPermissions().get(0).getCode())
        .isEqualTo(stubPermissionCode);
  }

  @Test
  void queryUniqueUserWithRolePermission_whenUserNotFound_shouldReturnEmpty() {
    UserRolePermissionDto mockResult = null;
    when(userRepository.fetchUniqueUserDtoWithNestedRolePermissionBy(anyLong()))
        .thenReturn(mockResult);
    UserRolePermissionDto userRolePermissionDto =
        identityAccessService.queryUniqueUserWithRolePermission(1L);
    assertThat(userRolePermissionDto).isNull();
  }

  @Test
  void pageQueryRole_whenRoleNotFound_shouldReturnEmpty() {
    Result<Record> mockRoleResult =
        dslContext.newResult(
            List.of(ROLE.ID, ROLE.NAME, ROLE.CODE, DSL.field("total_role", Integer.class)));
    when(roleRepository.pageFetchBy(any(PageRequestDto.class), any(RoleQueryDto.class)))
        .thenReturn(mockRoleResult);
    RoleQueryDto roleQueryDto = new RoleQueryDto();
    PageResponseDto<List<RoleDto>> pageResult =
        identityAccessService.pageQueryRole(PageRequestDto.of(1, 5), roleQueryDto);
    assertThat(pageResult.getTotal()).isEqualTo(0L);

    roleQueryDto.setUserId(1L);
    PageResponseDto<List<RoleDto>> pageResult2 =
        identityAccessService.pageQueryRole(PageRequestDto.of(1, 5), roleQueryDto);
    assertThat(pageResult2.getTotal()).isEqualTo(0L);
  }

  @Test
  void pageQueryPermission_givenRoleId_shouldReturnPermissionDto() {
    RolePermissionMap stubRolePermissionMap = new RolePermissionMap();
    stubRolePermissionMap.setRoleId(1L);
    stubRolePermissionMap.setPermissionId(1L);
    RolePermissionMap stubRolePermissionMap2 = new RolePermissionMap();
    stubRolePermissionMap2.setRoleId(1L);
    stubRolePermissionMap2.setPermissionId(2L);

    Result<Record> mockRoleResult =
        dslContext.newResult(
            List.of(
                PERMISSION.ID,
                PERMISSION.NAME,
                PERMISSION.CODE,
                DSL.field("total_permission", Integer.class)));
    mockRoleResult.addAll(
        List.of(
            dslContext
                .newRecord(
                    PERMISSION.ID,
                    PERMISSION.NAME,
                    PERMISSION.CODE,
                    DSL.field("total_permission", Integer.class))
                .values(1L, "vP0dKiHJpMsi", "vP0dKiHJpMsi", 2),
            dslContext
                .newRecord(
                    PERMISSION.ID,
                    PERMISSION.NAME,
                    PERMISSION.CODE,
                    DSL.field("total_permission", Integer.class))
                .values(2L, "NHQED41jQQ4C1IgG", "NHQED41jQQ4C1IgG", 2)));
    when(permissionRepository.pageFetchBy(any(PageRequestDto.class), any(PermissionQueryDto.class)))
        .thenReturn(mockRoleResult);
    PermissionQueryDto permissionQueryDto = new PermissionQueryDto();
    permissionQueryDto.setRoleId(1L);
    PageResponseDto<List<PermissionRespDto>> pageResult =
        identityAccessService.pageQueryPermission(PageRequestDto.of(1, 5), permissionQueryDto);
    assertThat(pageResult.getTotal()).isEqualTo(2L);
    List<PermissionRespDto> permissionResult = pageResult.getData();
    assertThat(permissionResult.get(0).getId()).isEqualTo(1L);
    assertThat(permissionResult.get(1).getId()).isEqualTo(2L);
  }

  @Test
  void pageQueryPermission_permissionNotFound_shouldReturnEmpty() {
    Result<Record> mockRoleResult =
        dslContext.newResult(
            List.of(
                PERMISSION.ID,
                PERMISSION.NAME,
                PERMISSION.CODE,
                DSL.field("total_permission", Integer.class)));
    when(permissionRepository.pageFetchBy(any(PageRequestDto.class), any(PermissionQueryDto.class)))
        .thenReturn(mockRoleResult);
    PermissionQueryDto permissionQueryDto = new PermissionQueryDto();
    PageResponseDto<List<PermissionRespDto>> pageResult =
        identityAccessService.pageQueryPermission(PageRequestDto.of(1, 5), permissionQueryDto);

    assertThat(pageResult.getTotal()).isEqualTo(0L);
    permissionQueryDto.setRoleId(1L);
    PageResponseDto<List<PermissionRespDto>> pageResult2 =
        identityAccessService.pageQueryPermission(PageRequestDto.of(1, 5), permissionQueryDto);
    assertThat(pageResult2.getTotal()).isEqualTo(0);
  }

  @Test
  void upsertUser_whenGivenUserDtoWithOutId_shouldCreatUser() {
    UserUpsertDto userUpsertDto = new UserUpsertDto();
    userUpsertDto.setUsername("username");
    userUpsertDto.setPassword("password");
    userUpsertDto.setEnable(true);
    User mockUser = new User();
    BeanUtils.copyProperties(userUpsertDto, mockUser);
    when(passwordEncoder.encode(anyString())).thenReturn("password");
    identityAccessService.upsertUser(userUpsertDto);
    verify(userRepository, times(1)).mergeWithoutNullFieldBy(mockUser);
  }

  @Test
  void upsertUser_whenGivenUserDtoWithId_shouldUpdateUser() {
    UserUpsertDto userUpsertDto = new UserUpsertDto();
    userUpsertDto.setId(1L);
    userUpsertDto.setUsername("username");
    userUpsertDto.setPassword("password");
    userUpsertDto.setEnable(true);
    User mockUser = new User();
    BeanUtils.copyProperties(userUpsertDto, mockUser);
    when(passwordEncoder.encode(anyString())).thenReturn("password");
    identityAccessService.upsertUser(userUpsertDto);
    verify(userRepository, times(1)).mergeWithoutNullFieldBy(mockUser);
  }

  @Test
  void upsertRole_whenGivenRoleDtoWithOutId_shouldCreateRole() {
    RoleUpsertDto roleUpsertDto = new RoleUpsertDto();
    roleUpsertDto.setCode("ROLE_ADMIN");
    roleUpsertDto.setName("Admin Role");

    Role mockRole = new Role();
    BeanUtils.copyProperties(roleUpsertDto, mockRole);

    identityAccessService.upsertRole(roleUpsertDto);

    verify(roleRepository, times(1)).merge(mockRole);
  }

  @Test
  void upsertRole_whenGivenRoleDtoWithId_shouldUpdateRole() {
    RoleUpsertDto roleUpsertDto = new RoleUpsertDto();
    roleUpsertDto.setId(1L);
    roleUpsertDto.setCode("ROLE_ADMIN");
    roleUpsertDto.setName("Admin Role");

    Role mockRole = new Role();
    BeanUtils.copyProperties(roleUpsertDto, mockRole);

    identityAccessService.upsertRole(roleUpsertDto);

    verify(roleRepository, times(1)).merge(mockRole);
  }

  @Test
  void upsertPermission_whenGivenPermissionDtoWithOutId_shouldCreatePermission() {
    PermissionUpsertDto permissionUpsertDto = new PermissionUpsertDto();
    permissionUpsertDto.setCode("PERM_READ");
    permissionUpsertDto.setName("Read Permission");

    Permission mockPermission = new Permission();
    BeanUtils.copyProperties(permissionUpsertDto, mockPermission);

    identityAccessService.upsertPermission(permissionUpsertDto);

    verify(permissionRepository, times(1)).merge(mockPermission);
  }

  @Test
  void upsertPermission_whenGivenPermissionDtoWithId_shouldUpdatePermission() {
    PermissionUpsertDto permissionUpsertDto = new PermissionUpsertDto();
    permissionUpsertDto.setId(1L);
    permissionUpsertDto.setCode("PERM_READ");
    permissionUpsertDto.setName("Read Permission");

    Permission mockPermission = new Permission();
    BeanUtils.copyProperties(permissionUpsertDto, mockPermission);

    identityAccessService.upsertPermission(permissionUpsertDto);

    verify(permissionRepository, times(1)).merge(mockPermission);
  }
}
