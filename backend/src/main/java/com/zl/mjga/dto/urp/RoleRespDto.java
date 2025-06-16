package com.zl.mjga.dto.urp;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.LinkedList;
import java.util.List;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RoleRespDto {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Long id;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String code;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Boolean isBound;

  @Builder.Default List<PermissionRespDto> permissions = new LinkedList<>();
}
