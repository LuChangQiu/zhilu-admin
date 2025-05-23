package com.zl.mjga.config.ai;

import com.zl.mjga.service.LlmService;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jooq.generated.default_schema.enums.LlmCodeEnum;
import org.jooq.generated.mjga.tables.pojos.AiLlmConfig;
import org.springframework.stereotype.Component;

@Data
@Component
@RequiredArgsConstructor
public class DeepSeekConfiguration {

  private String baseUrl;
  private String apiKey;
  private String modelName;

  private final LlmService llmService;

  @PostConstruct
  public void init() {
    AiLlmConfig aiLlmConfig = llmService.loadConfig(LlmCodeEnum.DEEP_SEEK);
    baseUrl = aiLlmConfig.getUrl();
    apiKey = aiLlmConfig.getApiKey();
    modelName = aiLlmConfig.getModelName();
  }
}
