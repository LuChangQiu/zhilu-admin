package com.zl.mjga.service;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

import com.zl.mjga.config.ai.ZhiPuEmbeddingModelConfig;
import com.zl.mjga.config.minio.MinIoConfig;
import com.zl.mjga.model.urp.Actions;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.loader.amazon.s3.AmazonS3DocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.*;
import dev.langchain4j.store.embedding.filter.Filter;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Configuration
@RequiredArgsConstructor
@Service
@Slf4j
public class EmbeddingService {

  private final EmbeddingModel zhipuEmbeddingModel;

  private final EmbeddingStore<TextSegment> zhiPuEmbeddingStore;

  private final ZhiPuEmbeddingModelConfig zhiPuEmbeddingModelConfig;

  private final AmazonS3DocumentLoader amazonS3DocumentLoader;

  private final EmbeddingStoreIngestor zhiPuEmbeddingStoreIngestor;

  private final MinIoConfig minIoConfig;

  public void ingestDocument(String objectName) {
    Document document =
        amazonS3DocumentLoader.loadDocument(
            minIoConfig.getDefaultBucket(), objectName, new ApacheTikaDocumentParser());
    IngestionResult ingest = zhiPuEmbeddingStoreIngestor.ingest(document);
    log.info("Ingest document finished {}", ingest);
  }

  public Map<String, String> searchAction(String message) {
    Map<String, String> result = new HashMap<>();
    EmbeddingSearchRequest embeddingSearchRequest =
        EmbeddingSearchRequest.builder()
            .queryEmbedding(zhipuEmbeddingModel.embed(message).content())
            .minScore(0.89)
            .build();
    EmbeddingSearchResult<TextSegment> embeddingSearchResult =
        zhiPuEmbeddingStore.search(embeddingSearchRequest);
    if (!embeddingSearchResult.matches().isEmpty()) {
      Metadata metadata = embeddingSearchResult.matches().getFirst().embedded().metadata();
      result.put(Actions.INDEX_KEY, metadata.getString(Actions.INDEX_KEY));
    }
    return result;
  }

  @PostConstruct
  public void initActionIndex() {
    if (!zhiPuEmbeddingModelConfig.getEnable()) {
      return;
    }
    for (Actions action : Actions.values()) {
      Embedding queryEmbedding = zhipuEmbeddingModel.embed(action.getContent()).content();
      Filter createUserFilter = metadataKey(Actions.INDEX_KEY).isEqualTo(action.getCode());
      EmbeddingSearchRequest embeddingSearchRequest =
          EmbeddingSearchRequest.builder()
              .queryEmbedding(queryEmbedding)
              .filter(createUserFilter)
              .build();
      EmbeddingSearchResult<TextSegment> embeddingSearchResult =
          zhiPuEmbeddingStore.search(embeddingSearchRequest);
      if (embeddingSearchResult.matches().isEmpty()) {
        TextSegment segment =
            TextSegment.from(
                action.getContent(), Metadata.metadata(Actions.INDEX_KEY, action.getCode()));
        Embedding embedding = zhipuEmbeddingModel.embed(segment).content();
        zhiPuEmbeddingStore.add(embedding, segment);
      }
    }
  }
}
