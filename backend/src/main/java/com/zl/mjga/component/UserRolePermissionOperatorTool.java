package com.zl.mjga.component;

import com.zl.mjga.dto.department.DepartmentBindDto;
import com.zl.mjga.dto.position.PositionBindDto;
import com.zl.mjga.dto.urp.PermissionUpsertDto;
import com.zl.mjga.dto.urp.RoleUpsertDto;
import com.zl.mjga.dto.urp.UserUpsertDto;
import com.zl.mjga.exception.BusinessException;
import com.zl.mjga.repository.*;
import com.zl.mjga.service.IdentityAccessService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.output.structured.Description;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jooq.generated.mjga.tables.pojos.*;
import org.springframework.stereotype.Component;

@Description("和用户管理有关的操作工具")
@RequiredArgsConstructor
@Component
public class UserRolePermissionOperatorTool {

  private final IdentityAccessService identityAccessService;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;
  private final DepartmentRepository departmentRepository;
  private final PositionRepository positionRepository;

  @Tool(value = {"创建用户", "入职申请", "开通账号"})
  void createUser(@P(value = "用户名") String name) {
    User user = userRepository.fetchOneByUsername(name);
    if (user != null) {
      throw new BusinessException("用户已存在");
    }
    identityAccessService.upsertUser(new UserUpsertDto(null, name, name, true));
  }

  //  @Tool(value = "查询用户")
  //  List<User> queryUser(@P(value = "用户名",required = false) String username, @P(value =
  // "开始日期",required = false) LocalDateTime startDate, @P(value = "结束日期",required = false)
  // LocalDateTime endDate) {
  //    return userRepository.fetchBy(new UserQueryDto(username, startDate, endDate));
  //  }

  @Tool(value = "删除用户")
  void deleteUser(@P(value = "用户名") String username) {
    userRepository.deleteByUsername(username);
  }

  @Tool(value = {"编辑用户", "更新用户", "更改用户"})
  void updateUser(
      @P(value = "用户名") String name,
      @P(value = "密码", required = false) String password,
      @P(value = "是否开启", required = false) Boolean enable) {
    identityAccessService.upsertUser(new UserUpsertDto(null, name, password, enable));
  }

  @Tool(value = {"给用户绑定角色", "给用户分配角色"})
  void bindRoleToUser(
      @P(value = "用户名") String username, @P(value = "角色名称") List<String> roleNames) {
    User user = checkUserExistBy(username);
    List<Long> bindRoleIds = getRoleIdsBy(roleNames);
    identityAccessService.bindRoleToUser(user.getId(), bindRoleIds);
  }

  @Tool(value = {"给用户解绑角色", "给用户撤销角色"})
  void unbindRoleToUser(
      @P(value = "用户名") String username, @P(value = "角色名称") List<String> roleNames) {
    User user = checkUserExistBy(username);
    List<Long> bindRoleIds = getRoleIdsBy(roleNames);
    identityAccessService.unBindRoleToUser(user.getId(), bindRoleIds);
  }

  @Tool(value = {"给用户绑定部门", "给用户分配部门"})
  void bindDepartmentToUser(
      @P(value = "用户名") String username, @P(value = "部门名称列表") List<String> departmentNames) {
    User user = checkUserExistBy(username);
    List<Department> departments =
        departmentRepository.fetchByName(departmentNames.toArray(String[]::new));
    if (departments.isEmpty()) {
      throw new BusinessException("指定部门不存在");
    }
    identityAccessService.bindDepartmentBy(
        new DepartmentBindDto(user.getId(), departments.stream().map(Department::getId).toList()));
  }

  @Tool(value = {"给用户解绑部门", "给用户撤销部门"})
  void unbindDepartmentToUser(
      @P(value = "用户名") String username, @P(value = "部门名称列表") List<String> departmentNames) {
    User user = checkUserExistBy(username);
    List<Department> departments =
        departmentRepository.fetchByName(departmentNames.toArray(String[]::new));
    if (departments.isEmpty()) {
      throw new BusinessException("指定部门不存在");
    }
    identityAccessService.unBindDepartmentBy(
        new DepartmentBindDto(user.getId(), departments.stream().map(Department::getId).toList()));
  }

  private User checkUserExistBy(String username) {
    User user = userRepository.fetchOneByUsername(username);
    if (user == null) {
      throw new BusinessException("指定用户不存在");
    }
    return user;
  }

  @Tool(value = {"给用户绑定岗位", "给用户分配岗位"})
  void bindPositionToUser(
      @P(value = "用户名") String username, @P(value = "岗位名称列表") List<String> positionNames) {
    User user = checkUserExistBy(username);
    List<Position> positions = positionRepository.fetchByName(positionNames.toArray(String[]::new));
    if (positions.isEmpty()) {
      throw new BusinessException("指定岗位不存在");
    }
    identityAccessService.bindPositionBy(
        new PositionBindDto(user.getId(), positions.stream().map(Position::getId).toList()));
  }

  @Tool(value = {"给用户解绑岗位", "给用户撤销岗位"})
  void unbindPositionToUser(
      @P(value = "用户名") String username, @P(value = "岗位名称列表") List<String> positionNames) {
    User user = checkUserExistBy(username);
    List<Position> positions = positionRepository.fetchByName(positionNames.toArray(String[]::new));
    if (positions.isEmpty()) {
      throw new BusinessException("指定岗位不存在");
    }
    identityAccessService.unBindPositionBy(
        new PositionBindDto(user.getId(), positions.stream().map(Position::getId).toList()));
  }

  private List<Long> getRoleIdsBy(List<String> roleNames) {
    List<Role> roles = roleRepository.fetchByName(roleNames.toArray(String[]::new));
    if (roles.isEmpty()) {
      throw new BusinessException("指定角色不存在");
    }
    return roles.stream().map(Role::getId).toList();
  }

  @Tool(value = {"创建角色", "创建系统角色"})
  void createRole(
      @P(value = "角色名称") String name, @P(value = "角色编码", required = false) String code) {
    if (StringUtils.isEmpty(code)) {
      code = name;
    }
    if (identityAccessService.isRoleDuplicate(code, name)) {
      throw new BusinessException("角色已存在");
    }
    identityAccessService.upsertRole(new RoleUpsertDto(null, name, code));
  }

  @Tool(value = "更新角色")
  void updateRole(@P(value = "角色名称") String name, @P(value = "角色编码") String code) {
    identityAccessService.upsertRole(new RoleUpsertDto(null, name, code));
  }

  @Tool(value = "更新权限")
  void updatePermission(@P(value = "权限名称") String name, @P(value = "权限编码") String code) {
    identityAccessService.upsertPermission(new PermissionUpsertDto(null, name, code));
  }

  @Tool(value = "删除角色")
  void deleteRole(@P(value = "角色名称") String name) {
    Role role = roleRepository.fetchOneByName(name);
    if (role == null) {
      throw new BusinessException("指定角色不存在");
    }
    roleRepository.deleteById(role.getId());
  }

  @Tool(value = "删除权限")
  void deletePermission(@P(value = "权限名称") String name) {
    Permission permission = permissionRepository.fetchOneByName(name);
    if (permission == null) {
      throw new BusinessException("指定权限不存在");
    }
    permissionRepository.deleteById(permission.getId());
  }

  @Tool(value = {"给角色绑定权限", "给用户分配权限"})
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

  @Tool(value = {"给角色解绑权限", "给角色撤销权限"})
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
      @P(value = "权限名称") String name, @P(value = "权限编码", required = false) String code) {
    if (StringUtils.isEmpty(code)) {
      code = name;
    }
    if (identityAccessService.isPermissionDuplicate(name, name)) {
      throw new BusinessException("权限已存在");
    }
    identityAccessService.upsertPermission(new PermissionUpsertDto(null, name, code));
  }
}
