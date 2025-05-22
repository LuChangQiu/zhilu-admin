package com.zl.mjga.config.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "zhipu")
public class ZhiPuConfiguration {

  private String baseUrl;
  private String apiKey;
  private String modelName;
}
