package com.zl.mjga.config.ai;

import dev.langchain4j.community.model.zhipu.ZhipuAiEmbeddingModel;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
public class EmbeddingConfig {

  @Resource private Environment env;

  @Bean
  @DependsOn("flywayInitializer")
  public EmbeddingModel zhipuEmbeddingModel(ZhiPuConfiguration zhiPuConfiguration) {
    return ZhipuAiEmbeddingModel.builder()
        .apiKey(zhiPuConfiguration.getApiKey())
        .model(zhiPuConfiguration.getEmbeddingModel())
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
