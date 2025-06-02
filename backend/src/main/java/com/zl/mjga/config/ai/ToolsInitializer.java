package com.zl.mjga.config.ai;

import com.zl.mjga.component.DepartmentOperatorTool;
import com.zl.mjga.component.UserOperatorTool;
import dev.langchain4j.community.model.zhipu.ZhipuAiStreamingChatModel;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@RequiredArgsConstructor
public class ToolsInitializer {

  private final UserOperatorTool userOperatorTool;
  private final DepartmentOperatorTool departmentOperatorTool;

  @Bean
  @DependsOn("flywayInitializer")
  public SystemToolAssistant zhiPuToolAssistant(ZhipuAiStreamingChatModel zhipuChatModel) {
    return AiServices.builder(SystemToolAssistant.class)
        .streamingChatModel(zhipuChatModel)
        .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
        .tools(userOperatorTool, departmentOperatorTool)
        .build();
  }
}
