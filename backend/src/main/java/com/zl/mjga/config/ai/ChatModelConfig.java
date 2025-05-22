package com.zl.mjga.config.ai;

import dev.langchain4j.community.model.zhipu.ZhipuAiStreamingChatModel;
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
  private final ZhiPuConfiguration zhiPuConfiguration;
  private final PromptConfiguration promptConfiguration;

  @Bean
  public ZhipuAiStreamingChatModel zhipuChatModel() {
    return ZhipuAiStreamingChatModel.builder()
        .model(zhiPuConfiguration.getModelName())
        .apiKey(zhiPuConfiguration.getApiKey())
        .logRequests(true)
        .logResponses(true)
        .build();
  }

  @Bean
  public OpenAiStreamingChatModel deepSeekChatModel() {
    return OpenAiStreamingChatModel.builder()
        .baseUrl(deepSeekConfiguration.getBaseUrl())
        .apiKey(deepSeekConfiguration.getApiKey())
        .modelName(deepSeekConfiguration.getModelName())
        .build();
  }

  @Bean
  public AiChatAssistant deepSeekChatAssistant(OpenAiStreamingChatModel deepSeekChatModel) {
    return AiServices.builder(AiChatAssistant.class)
        .streamingChatModel(deepSeekChatModel)
        .systemMessageProvider(chatMemoryId -> promptConfiguration.getSystem())
        .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
        .build();
  }

  @Bean
  public AiChatAssistant zhiPuChatAssistant(ZhipuAiStreamingChatModel zhipuChatModel) {
    return AiServices.builder(AiChatAssistant.class)
        .streamingChatModel(zhipuChatModel)
        .systemMessageProvider(chatMemoryId -> promptConfiguration.getSystem())
        .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
        .build();
  }
}
