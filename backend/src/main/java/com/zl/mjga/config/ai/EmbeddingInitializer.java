package com.zl.mjga.config.ai;

import com.zl.mjga.service.LlmService;
import dev.langchain4j.community.model.zhipu.ZhipuAiEmbeddingModel;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.jooq.generated.mjga.enums.LlmCodeEnum;
import org.jooq.generated.mjga.tables.pojos.AiLlmConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
public class EmbeddingInitializer {

  @Resource private Environment env;
  private final LlmService llmService;

  @Bean
  @DependsOn("flywayInitializer")
  public ZhiPuEmbeddingModelConfig zhiPuEmbeddingModelConfig() {
    ZhiPuEmbeddingModelConfig zhiPuEmbeddingModelConfig = new ZhiPuEmbeddingModelConfig();
    AiLlmConfig aiLlmConfig = llmService.loadConfig(LlmCodeEnum.ZHI_PU_EMBEDDING);
    zhiPuEmbeddingModelConfig.init(aiLlmConfig);
    return zhiPuEmbeddingModelConfig;
  }

  @Bean
  @DependsOn("flywayInitializer")
  public EmbeddingModel zhipuEmbeddingModel(ZhiPuEmbeddingModelConfig zhiPuEmbeddingModelConfig) {
    return ZhipuAiEmbeddingModel.builder()
        .apiKey(zhiPuEmbeddingModelConfig.getApiKey())
        .model(zhiPuEmbeddingModelConfig.getModelName())
        .dimensions(2048)
        .build();
  }

  @Bean
  public EmbeddingStore<TextSegment> zhiPuEmbeddingStore(EmbeddingModel zhipuEmbeddingModel) {
    String hostPort = env.getProperty("DATABASE_HOST_PORT");
    String host = hostPort.split(":")[0];
    return PgVectorEmbeddingStore.builder()
        .host(host)
        .port(env.getProperty("DATABASE_EXPOSE_PORT", Integer.class))
        .database(env.getProperty("DATABASE_DB"))
        .user(env.getProperty("DATABASE_USER"))
        .password(env.getProperty("DATABASE_PASSWORD"))
        .table("mjga.zhipu_embedding_store")
        .dimension(2048)
        .build();
  }
}
