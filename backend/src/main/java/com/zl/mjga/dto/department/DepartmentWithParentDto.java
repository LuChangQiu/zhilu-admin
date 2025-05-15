package com.zl.mjga.dto.department;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class DepartmentWithParentDto {
  @NotNull private Long id;
  @NotEmpty private String name;
  @NotEmpty Long parentId;
  @NotEmpty String parentName;
  @NotEmpty String path;
}
