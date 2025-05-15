package com.zl.mjga.service;

import static org.jooq.generated.mjga.tables.Department.DEPARTMENT;

import com.zl.mjga.dto.PageRequestDto;
import com.zl.mjga.dto.PageResponseDto;
import com.zl.mjga.dto.department.DepartmentQueryDto;
import com.zl.mjga.dto.department.DepartmentRespDto;
import com.zl.mjga.dto.department.DepartmentWithParentDto;
import com.zl.mjga.repository.DepartmentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.generated.mjga.tables.pojos.Department;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DepartmentService {

  private final DepartmentRepository departmentRepository;

  public List<Department> queryAvailableParentDepartmentsBy(Long id) {
    List<Department> allDepartments = departmentRepository.findAll();
    if (id != null) {
      List<DepartmentWithParentDto> departmentWithParentList = queryDepartmentAndSubsBy(id);
      allDepartments.removeIf(
          department -> {
            return departmentWithParentList.stream()
                .anyMatch(
                    (departmentWithParentDto -> {
                      return departmentWithParentDto.getId().equals(department.getId());
                    }));
          });
    }
    return allDepartments;
  }

  public void upsertDepartment(Department department) {
    departmentRepository.merge(department);
  }

  public List<DepartmentWithParentDto> queryDepartmentAndSubsBy(Long id) {
    return departmentRepository.queryDepartmentAndSubsBy(id);
  }

  public PageResponseDto<List<DepartmentRespDto>> pageQueryDepartment(
      PageRequestDto pageRequestDto, DepartmentQueryDto departmentQueryDto) {
    Result<Record> records = departmentRepository.pageFetchBy(pageRequestDto, departmentQueryDto);
    if (records.isEmpty()) {
      return PageResponseDto.empty();
    }
    List<DepartmentRespDto> departments =
        records.map(
            record -> {
              return DepartmentRespDto.builder()
                  .id(record.getValue(DEPARTMENT.ID))
                  .name(record.getValue(DEPARTMENT.NAME))
                  .parentId(record.getValue(DEPARTMENT.PARENT_ID))
                  .isBound(
                      record.field("is_bound") != null
                          ? record.get("is_bound", Boolean.class)
                          : null)
                  .parentName(record.get("parent_name", String.class))
                  .build();
            });
    Long totalDepartment = records.get(0).getValue("total_department", Long.class);
    return new PageResponseDto<>(totalDepartment, departments);
  }
}
