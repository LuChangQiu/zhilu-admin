package com.zl.mjga.config.ai;

import com.zl.mjga.service.LlmService;
import dev.langchain4j.community.model.zhipu.ZhipuAiStreamingChatModel;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import org.jooq.generated.mjga.enums.LlmCodeEnum;
import org.jooq.generated.mjga.tables.pojos.AiLlmConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@RequiredArgsConstructor
public class ChatModelConfig {

  private final LlmService llmService;
  private final PromptConfiguration promptConfiguration;

  @Bean
  @DependsOn("flywayInitializer")
  public ZhipuAiStreamingChatModel zhipuChatModel(ZhiPuConfiguration zhiPuConfiguration) {
    return ZhipuAiStreamingChatModel.builder()
        .model(zhiPuConfiguration.getModelName())
        .apiKey(zhiPuConfiguration.getApiKey())
        .logRequests(true)
        .logResponses(true)
        .build();
  }

  @Bean
  @DependsOn("flywayInitializer")
  public OpenAiStreamingChatModel deepSeekChatModel(DeepSeekConfiguration deepSeekConfiguration) {
    return OpenAiStreamingChatModel.builder()
        .baseUrl(deepSeekConfiguration.getBaseUrl())
        .apiKey(deepSeekConfiguration.getApiKey())
        .modelName(deepSeekConfiguration.getModelName())
        .build();
  }

  @Bean
  @DependsOn("flywayInitializer")
  public AiChatAssistant deepSeekChatAssistant(OpenAiStreamingChatModel deepSeekChatModel) {
    return AiServices.builder(AiChatAssistant.class)
        .streamingChatModel(deepSeekChatModel)
        .systemMessageProvider(chatMemoryId -> promptConfiguration.getSystem())
        .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
        .build();
  }

  @Bean
  @DependsOn("flywayInitializer")
  public AiChatAssistant zhiPuChatAssistant(ZhipuAiStreamingChatModel zhipuChatModel) {
    return AiServices.builder(AiChatAssistant.class)
        .streamingChatModel(zhipuChatModel)
        .systemMessageProvider(chatMemoryId -> promptConfiguration.getSystem())
        .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
        .build();
  }

  @Bean
  @DependsOn("flywayInitializer")
  public DeepSeekConfiguration deepSeekConfiguration() {
    DeepSeekConfiguration deepSeekConfiguration = new DeepSeekConfiguration();
    AiLlmConfig deepSeek = llmService.loadConfig(LlmCodeEnum.DEEP_SEEK);
    deepSeekConfiguration.init(deepSeek);
    return deepSeekConfiguration;
  }

  @Bean
  @DependsOn("flywayInitializer")
  public ZhiPuConfiguration zhiPuConfiguration() {
    ZhiPuConfiguration zhiPuConfiguration = new ZhiPuConfiguration();
    AiLlmConfig aiLlmConfig = llmService.loadConfig(LlmCodeEnum.ZHI_PU);
    zhiPuConfiguration.init(aiLlmConfig);
    return zhiPuConfiguration;
  }
}
