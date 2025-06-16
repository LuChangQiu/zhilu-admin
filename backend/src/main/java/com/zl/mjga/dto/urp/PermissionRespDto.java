package com.zl.mjga.dto.urp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PermissionRespDto {

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Long id;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String code;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Boolean isBound;
}
