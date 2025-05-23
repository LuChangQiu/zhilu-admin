package com.zl.mjga.dto.ai;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LlmUpdateDto {
  @NotNull private Long id;
  @NotEmpty private String name;
  @NotEmpty private String modelName;
  @NotEmpty private String apiKey;
  @NotEmpty private String url;
  @NotNull private Boolean enable;
  @NotNull private Short priority;
}
