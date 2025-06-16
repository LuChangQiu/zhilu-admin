package com.zl.mjga.component;

import com.zl.mjga.exception.BusinessException;
import com.zl.mjga.repository.DepartmentRepository;
import com.zl.mjga.service.DepartmentService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.output.structured.Description;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jooq.generated.mjga.tables.pojos.Department;
import org.springframework.stereotype.Component;

@Description("和部门管理有关的操作工具")
@RequiredArgsConstructor
@Component
public class DepartmentOperatorTool {

  private final DepartmentService departmentService;
  private final DepartmentRepository departmentRepository;

  @Tool(value = "创建部门")
  void createDepartment(
      @P(value = "部门名称") @Size(min = 2, max = 15) String name,
      @P(value = "上级部门名称", required = false) @Size(min = 2, max = 15) String parentName) {
    Department exist = departmentRepository.fetchOneByName(name);
    Department department = new Department(null, name, null);
    if (exist != null) {
      throw new BusinessException("当前部门已存在");
    }
    if (StringUtils.isNotEmpty(parentName)) {
      Department parent = departmentRepository.fetchOneByName(parentName);
      if (parent == null) {
        throw new BusinessException("指定的上级部门不存在");
      } else {
        department.setParentId(parent.getId());
      }
    }
    departmentService.upsertDepartment(department);
  }

  @Tool(value = {"更新部门信息"})
  void updateDepartment(@P(value = "部门名称") String name) {
    Department exist = departmentRepository.fetchOneByName(name);
    if (exist == null) {
      throw new BusinessException("不存在的部门");
    }
    exist.setName(name);
    departmentService.upsertDepartment(exist);
  }

  @Tool(value = {"给指定部门绑定/分配上级部门"})
  void bindParentDepartment(
      @P(value = "部门名称") String name, @P(value = "上级部门名称") String parentName) {
    Department exist = departmentRepository.fetchOneByName(name);
    if (exist == null) {
      throw new BusinessException("不存在的部门");
    }
    Department parent = departmentRepository.fetchOneByName(parentName);
    if (parent == null) {
      throw new BusinessException("上级部门不存在");
    }
    exist.setParentId(parent.getId());
    departmentService.upsertDepartment(exist);
  }

  @Tool(value = {"给指定部门解绑/撤销上级部门"})
  void unbindParentDepartment(@P(value = "部门名称") String name) {
    Department exist = departmentRepository.fetchOneByName(name);
    if (exist == null) {
      throw new BusinessException("不存在的部门");
    }
    exist.setParentId(null);
    departmentService.upsertDepartment(exist);
  }

  @Tool(value = "删除指定部门")
  void deleteDepartment(@P(value = "部门名称") String name) {
    Department exist = departmentRepository.fetchOneByName(name);
    if (exist == null) {
      throw new BusinessException("不存在的部门");
    }
    departmentRepository.deleteById(exist.getId());
  }
}
