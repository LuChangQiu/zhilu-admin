package com.zl.mjga.config.ai;

import com.zl.mjga.component.PromptConfiguration;
import com.zl.mjga.service.LlmService;
import dev.langchain4j.community.model.zhipu.ZhipuAiStreamingChatModel;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.jooq.generated.mjga.enums.LlmCodeEnum;
import org.jooq.generated.mjga.tables.pojos.AiLlmConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@RequiredArgsConstructor
public class ChatModelInitializer {

  private final LlmService llmService;
  private final PromptConfiguration promptConfiguration;

  @Bean
  @DependsOn("flywayInitializer")
  public ZhipuAiStreamingChatModel zhipuChatModel(ZhiPuChatModelConfig zhiPuChatModelConfig) {
    return ZhipuAiStreamingChatModel.builder()
        .model(zhiPuChatModelConfig.getModelName())
        .apiKey(zhiPuChatModelConfig.getApiKey())
        .logRequests(true)
        .logResponses(true)
        .build();
  }

  @Bean
  @DependsOn("flywayInitializer")
  public OpenAiStreamingChatModel deepSeekChatModel(
      DeepSeekChatModelConfig deepSeekChatModelConfig) {
    return OpenAiStreamingChatModel.builder()
        .baseUrl(deepSeekChatModelConfig.getBaseUrl())
        .apiKey(deepSeekChatModelConfig.getApiKey())
        .modelName(deepSeekChatModelConfig.getModelName())
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
  public AiChatAssistant zhiPuChatAssistant(
      ZhipuAiStreamingChatModel zhipuChatModel,
      EmbeddingStore<TextSegment> zhiPuLibraryEmbeddingStore) {
    return AiServices.builder(AiChatAssistant.class)
        .streamingChatModel(zhipuChatModel)
        .systemMessageProvider(chatMemoryId -> promptConfiguration.getSystem())
        .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
        .contentRetriever(EmbeddingStoreContentRetriever.from(zhiPuLibraryEmbeddingStore))
        .build();
  }

  @Bean
  @DependsOn("flywayInitializer")
  public DeepSeekChatModelConfig deepSeekConfiguration() {
    DeepSeekChatModelConfig deepSeekChatModelConfig = new DeepSeekChatModelConfig();
    AiLlmConfig deepSeek = llmService.loadConfig(LlmCodeEnum.DEEP_SEEK);
    deepSeekChatModelConfig.init(deepSeek);
    return deepSeekChatModelConfig;
  }

  @Bean
  @DependsOn("flywayInitializer")
  public ZhiPuChatModelConfig zhiPuConfiguration() {
    ZhiPuChatModelConfig zhiPuChatModelConfig = new ZhiPuChatModelConfig();
    AiLlmConfig aiLlmConfig = llmService.loadConfig(LlmCodeEnum.ZHI_PU);
    zhiPuChatModelConfig.init(aiLlmConfig);
    return zhiPuChatModelConfig;
  }
}
