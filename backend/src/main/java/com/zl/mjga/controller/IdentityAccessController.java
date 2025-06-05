package com.zl.mjga.controller;

import com.zl.mjga.dto.PageRequestDto;
import com.zl.mjga.dto.PageResponseDto;
import com.zl.mjga.dto.department.DepartmentBindDto;
import com.zl.mjga.dto.permission.PermissionBindDto;
import com.zl.mjga.dto.position.PositionBindDto;
import com.zl.mjga.dto.role.RoleBindDto;
import com.zl.mjga.dto.urp.*;
import com.zl.mjga.exception.BusinessException;
import com.zl.mjga.repository.PermissionRepository;
import com.zl.mjga.repository.RoleRepository;
import com.zl.mjga.repository.UserRepository;
import com.zl.mjga.service.IdentityAccessService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.generated.mjga.tables.pojos.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.*;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@RestController
@RequestMapping("/iam")
@RequiredArgsConstructor
public class IdentityAccessController {

  private final IdentityAccessService identityAccessService;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;

  @GetMapping("/me")
  UserRolePermissionDto currentUser(Principal principal) {
    String name = principal.getName();
    User user = userRepository.fetchOneByUsername(name);
    if (!user.getEnable()) {
      throw new DisabledException(String.format("用户 %s 被禁用", name));
    }
    return identityAccessService.queryUniqueUserWithRolePermission(user.getId());
  }

  @PostMapping("/me")
  void upsertMe(Principal principal, @RequestBody UserUpsertDto userUpsertDto) {
    String name = principal.getName();
    User user = userRepository.fetchOneByUsername(name);
    userUpsertDto.setId(user.getId());
    identityAccessService.upsertUser(userUpsertDto);
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_USER_ROLE_PERMISSION)")
  @PostMapping("/user")
  void upsertUser(@RequestBody @Valid UserUpsertDto userUpsertDto) {
    identityAccessService.upsertUser(userUpsertDto);
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).READ_USER_ROLE_PERMISSION)")
  @GetMapping("/user")
  UserRolePermissionDto queryUserWithRolePermission(@RequestParam Long userId) {
    return identityAccessService.queryUniqueUserWithRolePermission(userId);
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_USER_ROLE_PERMISSION)")
  @DeleteMapping("/user")
  void deleteUser(@RequestParam Long userId) {
    userRepository.deleteById(userId);
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_USER_ROLE_PERMISSION)")
  @PostMapping("/role")
  void upsertRole(@RequestBody RoleUpsertDto roleUpsertDto) {
    identityAccessService.upsertRole(roleUpsertDto);
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_USER_ROLE_PERMISSION)")
  @DeleteMapping("/role")
  void deleteRole(@RequestParam Long roleId) {
    roleRepository.deleteById(roleId);
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_USER_ROLE_PERMISSION)")
  @GetMapping("/role")
  RoleDto queryRoleWithPermission(@RequestParam Long roleId) {
    return identityAccessService.queryUniqueRoleWithPermission(roleId);
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_USER_ROLE_PERMISSION)")
  @PostMapping("/permission")
  void upsertPermission(@RequestBody PermissionUpsertDto permissionUpsertDto) {
    identityAccessService.upsertPermission(permissionUpsertDto);
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_USER_ROLE_PERMISSION)")
  @DeleteMapping("/permission")
  void deletePermission(@RequestParam Long permissionId) {
    permissionRepository.deleteById(permissionId);
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).READ_USER_ROLE_PERMISSION)")
  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  PageResponseDto<List<UserRolePermissionDto>> queryUsers(
      @ModelAttribute PageRequestDto pageRequestDto, @ModelAttribute UserQueryDto userQueryDto) {
    return identityAccessService.pageQueryUser(pageRequestDto, userQueryDto);
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).READ_USER_ROLE_PERMISSION)")
  @GetMapping("/roles")
  @ResponseStatus(HttpStatus.OK)
  PageResponseDto<List<RoleDto>> queryRoles(
      @ModelAttribute PageRequestDto pageRequestDto, @ModelAttribute RoleQueryDto roleQueryDto) {
    return identityAccessService.pageQueryRole(pageRequestDto, roleQueryDto);
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).READ_USER_ROLE_PERMISSION)")
  @GetMapping("/permissions")
  @ResponseStatus(HttpStatus.OK)
  PageResponseDto<List<PermissionRespDto>> queryPermissions(
      @ModelAttribute PageRequestDto pageRequestDto,
      @ModelAttribute PermissionQueryDto permissionQueryDto) {
    return identityAccessService.pageQueryPermission(pageRequestDto, permissionQueryDto);
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_USER_ROLE_PERMISSION)")
  @PostMapping("/role/bind")
  @ResponseStatus(HttpStatus.OK)
  void bindRoleBy(@RequestBody @Valid RoleBindDto roleBindDto) {
    identityAccessService.bindRoleToUser(roleBindDto.userId(), roleBindDto.roleIds());
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_USER_ROLE_PERMISSION)")
  @PostMapping("/role/unbind")
  @ResponseStatus(HttpStatus.OK)
  void unBindRoleBy(@RequestBody @Valid RoleBindDto roleBindDto) {
    if (roleBindDto.userId() == 1) {
      throw new BusinessException("演示系统不允许操作管理员");
    }
    identityAccessService.unBindRoleToUser(roleBindDto.userId(), roleBindDto.roleIds());
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_USER_ROLE_PERMISSION)")
  @PostMapping("/permission/bind")
  @ResponseStatus(HttpStatus.OK)
  void bindPermissionBy(@RequestBody @Valid PermissionBindDto permissionBindDto) {
    identityAccessService.bindPermissionBy(
        permissionBindDto.roleId(), permissionBindDto.permissionIds());
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_USER_ROLE_PERMISSION)")
  @PostMapping("/permission/unbind")
  @ResponseStatus(HttpStatus.OK)
  void unBindPermissionBy(@RequestBody @Valid PermissionBindDto permissionBindDto) {
    if (permissionBindDto.roleId() == 1) {
      throw new BusinessException("演示系统不允许操作管理员角色");
    }
    identityAccessService.unBindPermissionBy(
        permissionBindDto.roleId(), permissionBindDto.permissionIds());
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_DEPARTMENT_PERMISSION)")
  @PostMapping("/department/bind")
  @ResponseStatus(HttpStatus.OK)
  public void bindDepartmentBy(@RequestBody @Valid DepartmentBindDto departmentBindDto) {
    identityAccessService.bindDepartmentBy(departmentBindDto);
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_DEPARTMENT_PERMISSION)")
  @PostMapping("/department/unbind")
  @ResponseStatus(HttpStatus.OK)
  public void unBindDepartmentBy(@RequestBody @Valid DepartmentBindDto departmentBindDto) {
    identityAccessService.unBindDepartmentBy(departmentBindDto);
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_DEPARTMENT_PERMISSION)")
  @PostMapping("/position/bind")
  @ResponseStatus(HttpStatus.OK)
  public void bindPositionBy(@RequestBody @Valid PositionBindDto positionBindDto) {
    identityAccessService.bindPositionBy(positionBindDto);
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_DEPARTMENT_PERMISSION)")
  @PostMapping("/position/unbind")
  @ResponseStatus(HttpStatus.OK)
  public void unBindPositionBy(@RequestBody @Valid PositionBindDto positionBindDto) {
    identityAccessService.unBindPositionBy(positionBindDto);
  }
}
