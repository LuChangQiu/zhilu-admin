package com.zl.mjga.dto.urp;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserRolePermissionDto {

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Long id;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String username;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  private String avatar;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Boolean enable;

  @Builder.Default private List<RoleRespDto> roles = new LinkedList<>();

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private OffsetDateTime createTime;

  public Set<PermissionRespDto> getPermissions() {
    return roles.stream()
        .flatMap((roleDto) -> roleDto.getPermissions().stream())
        .collect(Collectors.toSet());
  }
}
