package com.zl.mjga.config.ai;

import com.zl.mjga.exception.BusinessException;
import com.zl.mjga.repository.DepartmentRepository;
import com.zl.mjga.service.DepartmentService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.output.structured.Description;
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
      @P(value = "部门名称") String departmentName,
      @P(value = "上级部门名称", required = false) String parentDepartmentName) {
    Department exist = departmentRepository.fetchOneByName(departmentName);
    if (exist != null) {
      throw new BusinessException("当前部门已存在");
    }
    if (StringUtils.isNotEmpty(parentDepartmentName)) {
      Department parent = departmentRepository.fetchOneByName(parentDepartmentName);
      if (parent == null) {
        throw new BusinessException("上级部门不存在");
      }
    }
    departmentService.upsertDepartment(new Department(null, departmentName, null));
  }
}
