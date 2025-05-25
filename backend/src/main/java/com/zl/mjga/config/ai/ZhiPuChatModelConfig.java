package com.zl.mjga.config.ai;

import lombok.Data;
import org.jooq.generated.mjga.tables.pojos.AiLlmConfig;

@Data
public class ZhiPuChatModelConfig {

  private String baseUrl;
  private String apiKey;
  private String modelName;

  public void init(AiLlmConfig config) {
    this.baseUrl = config.getUrl();
    this.apiKey = config.getApiKey();
    this.modelName = config.getModelName();
  }
}
