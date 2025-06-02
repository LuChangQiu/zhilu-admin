package com.zl.mjga.component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.Data;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Data
@Component
public class PromptConfiguration {

  @jakarta.annotation.Resource private ResourceLoader resourceLoader;
  private String system;

  @PostConstruct
  public void init() throws IOException {
    Resource resource = resourceLoader.getResource("classpath:prompt.txt");
    system = Files.readString(Paths.get(resource.getURI()));
  }
}
