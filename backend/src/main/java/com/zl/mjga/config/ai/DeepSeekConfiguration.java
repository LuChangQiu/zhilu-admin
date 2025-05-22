package com.zl.mjga.config.ai;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Data
@Component
@ConfigurationProperties(prefix = "deep-seek")
public class DeepSeekConfiguration {

  @jakarta.annotation.Resource
  private ResourceLoader resourceLoader;

  private String baseUrl;
  private String apiKey;
  private Prompt prompt;
  private String modelName;

  @Data
  public static class Prompt {
    private String system;
  }

  @PostConstruct
  public void init() throws IOException {
    Resource resource = resourceLoader.getResource("classpath:prompt.txt");
    prompt = new Prompt();
    prompt.setSystem(Files.readString(Paths.get(resource.getURI())));
  }
}

