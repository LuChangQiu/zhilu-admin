package com.zl.mjga.config.ai;

import lombok.Data;
import org.jooq.generated.mjga.tables.pojos.AiLlmConfig;
import org.springframework.stereotype.Component;

@Data
@Component
public class ZhiPuConfiguration {

  private String baseUrl;
  private String apiKey;
  private String modelName;
  private String embeddingModel;

  public void init(AiLlmConfig config) {
    this.baseUrl = config.getUrl();
    this.apiKey = config.getApiKey();
    this.modelName = config.getModelName();
    this.embeddingModel = config.getEmbeddingModel();
  }
}
