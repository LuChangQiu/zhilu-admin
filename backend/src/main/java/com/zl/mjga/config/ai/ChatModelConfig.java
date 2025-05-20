package com.zl.mjga.config.ai;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ChatModelConfig {

  private final DeepSeekConfiguration deepSeekConfiguration;

  @Bean
  public OpenAiStreamingChatModel deepSeekChatModel() {
    return OpenAiStreamingChatModel.builder()
        .baseUrl(deepSeekConfiguration.getBaseUrl())
        .apiKey(deepSeekConfiguration.getApiKey())
        .modelName(deepSeekConfiguration.getModelName())
        .build();
  }

  @Bean
  public DeepSeekChatAssistant deepSeekChatAssistant(OpenAiStreamingChatModel deepSeekChatModel) {
    return AiServices.builder(DeepSeekChatAssistant.class)
        .streamingChatModel(deepSeekChatModel)
        .systemMessageProvider(chatMemoryId -> "你是一个叫做「知路 AI」的企业级 AI 助手，能帮助用户回答任何问题。")
        .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
        .build();
  }
}
