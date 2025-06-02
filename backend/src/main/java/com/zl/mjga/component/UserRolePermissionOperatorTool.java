package com.zl.mjga.component;

import com.zl.mjga.dto.urp.PermissionUpsertDto;
import com.zl.mjga.dto.urp.RoleUpsertDto;
import com.zl.mjga.dto.urp.UserUpsertDto;
import com.zl.mjga.exception.BusinessException;
import com.zl.mjga.repository.PermissionRepository;
import com.zl.mjga.repository.RoleRepository;
import com.zl.mjga.repository.UserRepository;
import com.zl.mjga.service.IdentityAccessService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.output.structured.Description;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jooq.generated.mjga.tables.pojos.Permission;
import org.jooq.generated.mjga.tables.pojos.Role;
import org.jooq.generated.mjga.tables.pojos.User;
import org.springframework.stereotype.Component;

@Description("和用户管理有关的操作工具")
@RequiredArgsConstructor
@Component
public class UserRolePermissionOperatorTool {

  private final IdentityAccessService identityAccessService;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;

  @Tool(value = "创建用户或注册用户")
  void createUser(@P(value = "用户名") String username) {
    User user = userRepository.fetchOneByUsername(username);
    if (user != null) {
      throw new BusinessException("用户已存在");
    }
    identityAccessService.upsertUser(new UserUpsertDto(null, username, username, true));
  }

  @Tool(value = "删除用户")
  void deleteUser(@P(value = "用户名") String username) {
    userRepository.deleteByUsername(username);
  }

  @Tool(value = "编辑/更新/更改用户")
  void updateUser(
      @P(value = "用户名") String username,
      @P(value = "密码", required = false) String password,
      @P(value = "是否开启", required = false) Boolean enable) {
    identityAccessService.upsertUser(new UserUpsertDto(null, username, password, enable));
  }

  @Tool(value = "给用户绑定/分配角色")
  void bindRoleToUser(
      @P(value = "用户名") String username, @P(value = "角色名称") List<String> roleNames) {
    User user = userRepository.fetchOneByUsername(username);
    if (user == null) {
      throw new BusinessException("指定用户不存在");
    }
    List<Long> bindRoleIds = getRoleIdsBy(roleNames);
    identityAccessService.bindRoleToUser(user.getId(), bindRoleIds);
  }

  @Tool(value = "给用户解绑/撤销角色")
  void unbindRoleToUser(
      @P(value = "用户名") String username, @P(value = "角色名称") List<String> roleNames) {
    User user = userRepository.fetchOneByUsername(username);
    if (user == null) {
      throw new BusinessException("指定用户不存在");
    }
    List<Long> bindRoleIds = getRoleIdsBy(roleNames);
    identityAccessService.unBindRoleToUser(user.getId(), bindRoleIds);
  }

  private List<Long> getRoleIdsBy(List<String> roleNames) {
    List<Role> roles = roleRepository.fetchByName(roleNames.toArray(String[]::new));
    if (roles.isEmpty()) {
      throw new BusinessException("指定角色不存在");
    }
    return roles.stream().map(Role::getId).toList();
  }

  @Tool(value = "创建角色")
  void createRole(
      @P(value = "角色名称") String roleName, @P(value = "角色编码", required = false) String roleCode) {
    if (StringUtils.isEmpty(roleCode)) {
      roleCode = roleName;
    }
    if (identityAccessService.isRoleDuplicate(roleCode, roleName)) {
      throw new BusinessException("角色已存在");
    }
    identityAccessService.upsertRole(new RoleUpsertDto(null, roleName, roleCode));
  }

  @Tool(value = "更新角色")
  void updateRole(@P(value = "角色名称") String roleName, @P(value = "角色编码") String roleCode) {
    identityAccessService.upsertRole(new RoleUpsertDto(null, roleName, roleCode));
  }

  @Tool(value = "更新权限")
  void updatePermission(
      @P(value = "权限名称") String permissionName, @P(value = "权限编码") String permissionCode) {
    identityAccessService.upsertPermission(
        new PermissionUpsertDto(null, permissionName, permissionCode));
  }

  @Tool(value = "删除角色")
  void deleteRole(@P(value = "角色名称") String roleName) {
    Role role = roleRepository.fetchOneByName(roleName);
    if (role == null) {
      throw new BusinessException("指定角色不存在");
    }
    roleRepository.deleteById(role.getId());
  }

  @Tool(value = "删除权限")
  void deletePermission(@P(value = "权限名称") String permissionName) {
    Permission permission = permissionRepository.fetchOneByName(permissionName);
    if (permission == null) {
      throw new BusinessException("指定权限不存在");
    }
    permissionRepository.deleteById(permission.getId());
  }

  @Tool(value = "给角色绑定/分配权限")
  void bindPermissionToRole(
      @P(value = "角色名称") String roleName, @P(value = "权限名称") List<String> permissionNames) {
    Role role = roleRepository.fetchOneByName(roleName);
    if (role == null) {
      throw new BusinessException("指定角色不存在");
    }
    List<Permission> permissions =
        permissionRepository.fetchByName(permissionNames.toArray(String[]::new));
    if (permissions.isEmpty()) {
      throw new BusinessException("指定权限不存在");
    }
    identityAccessService.bindPermissionBy(
        role.getId(), permissions.stream().map(Permission::getId).toList());
  }

  @Tool(value = "给角色解绑/撤销权限")
  void unBindPermissionToRole(
      @P(value = "角色名称") String roleName, @P(value = "权限名称") List<String> permissionNames) {
    Role role = roleRepository.fetchOneByName(roleName);
    if (role == null) {
      throw new BusinessException("指定角色不存在");
    }
    List<Permission> permissions =
        permissionRepository.fetchByName(permissionNames.toArray(String[]::new));
    if (permissions.isEmpty()) {
      throw new BusinessException("指定权限不存在");
    }
    identityAccessService.unBindPermissionBy(
        role.getId(), permissions.stream().map(Permission::getId).toList());
  }

  @Tool(value = "创建权限")
  void createPermission(
      @P(value = "权限名称") String permissionName,
      @P(value = "权限编码", required = false) String permissionCode) {
    if (StringUtils.isEmpty(permissionCode)) {
      permissionCode = permissionName;
    }
    if (identityAccessService.isPermissionDuplicate(permissionName, permissionName)) {
      throw new BusinessException("权限已存在");
    }
    identityAccessService.upsertPermission(
        new PermissionUpsertDto(null, permissionName, permissionCode));
  }
}
