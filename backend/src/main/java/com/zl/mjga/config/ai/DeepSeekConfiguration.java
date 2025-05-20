package com.zl.mjga.config.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "deep-seek")
public class DeepSeekConfiguration {

  private String baseUrl;
  private String apiKey;
  private Prompt prompt;
  private String modelName;

  @Data
  public static class Prompt {
    private String system;
  }
}
