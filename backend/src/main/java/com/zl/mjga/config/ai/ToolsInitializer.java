package com.zl.mjga.config.ai;

import com.zl.mjga.component.DepartmentOperatorTool;
import com.zl.mjga.component.PositionOperatorTool;
import com.zl.mjga.component.UserRolePermissionOperatorTool;
import dev.langchain4j.community.model.zhipu.ZhipuAiStreamingChatModel;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@RequiredArgsConstructor
public class ToolsInitializer {

  private final UserRolePermissionOperatorTool userRolePermissionOperatorTool;
  private final DepartmentOperatorTool departmentOperatorTool;
  private final PositionOperatorTool positionOperatorTool;

  @Bean
  @DependsOn("flywayInitializer")
  public SystemToolAssistant zhiPuToolAssistant(ZhipuAiStreamingChatModel zhipuChatModel) {
    return AiServices.builder(SystemToolAssistant.class)
        .streamingChatModel(zhipuChatModel)
        .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
        .tools(userRolePermissionOperatorTool, departmentOperatorTool, positionOperatorTool)
        .build();
  }

  @Bean
  @DependsOn("flywayInitializer")
  public SystemToolAssistant deepSeekToolAssistant(OpenAiStreamingChatModel deepSeekChatModel) {
    return AiServices.builder(SystemToolAssistant.class)
        .streamingChatModel(deepSeekChatModel)
        .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
        .tools(userRolePermissionOperatorTool, departmentOperatorTool, positionOperatorTool)
        .build();
  }
}
