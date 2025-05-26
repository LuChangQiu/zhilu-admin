package com.zl.mjga.dto.ai;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LlmVm {
  @NotNull private Long id;

  @NotEmpty(message = "名称不能为空") private String name;

  @NotEmpty(message = "模型名称不能为空") private String modelName;

  @NotEmpty(message = "模型类型不能为空") private String type;

  @NotEmpty(message = "apikey 不能为空") private String apiKey;

  @NotEmpty(message = "url 不能为空") private String url;

  @NotNull(message = "是否启用不能为空") private Boolean enable;

  @NotNull(message = "优先级不能为空") private Short priority;
}
