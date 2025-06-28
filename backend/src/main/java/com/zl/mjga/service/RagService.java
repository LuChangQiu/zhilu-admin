package com.zl.mjga.service;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zl.mjga.config.ai.ZhiPuEmbeddingModelConfig;
import com.zl.mjga.config.minio.MinIoConfig;
import com.zl.mjga.model.urp.Actions;
import com.zl.mjga.repository.LibraryDocRepository;
import com.zl.mjga.repository.LibraryRepository;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.loader.amazon.s3.AmazonS3DocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.*;
import dev.langchain4j.store.embedding.filter.Filter;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.jooq.JSON;
import org.jooq.generated.mjga.enums.LibraryDocStatusEnum;
import org.jooq.generated.mjga.tables.daos.LibraryDocSegmentDao;
import org.jooq.generated.mjga.tables.pojos.LibraryDoc;
import org.jooq.generated.mjga.tables.pojos.LibraryDocSegment;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Configuration
@RequiredArgsConstructor
@Service
@Slf4j
public class RagService {

  private final EmbeddingModel zhipuEmbeddingModel;

  private final EmbeddingStore<TextSegment> zhiPuEmbeddingStore;

  private final EmbeddingStore<TextSegment> zhiPuLibraryEmbeddingStore;

  private final ZhiPuEmbeddingModelConfig zhiPuEmbeddingModelConfig;

  private final AmazonS3DocumentLoader amazonS3DocumentLoader;

  private final MinIoConfig minIoConfig;

  private final LibraryRepository libraryRepository;

  private final LibraryDocRepository libraryDocRepository;

  private final LibraryDocSegmentDao libraryDocSegmentDao;

  public void deleteLibraryBy(Long libraryId) {
    List<LibraryDoc> libraryDocs = libraryDocRepository.fetchByLibId(libraryId);
    List<Long> docIds = libraryDocs.stream().map(LibraryDoc::getId).toList();
    for (Long docId : docIds) {
      deleteDocBy(docId);
    }
    libraryRepository.deleteById(libraryId);
  }

  public void deleteDocBy(Long docId) {
    List<LibraryDocSegment> libraryDocSegments = libraryDocSegmentDao.fetchByDocId(docId);
    List<String> embeddingIdList =
        libraryDocSegments.stream().map(LibraryDocSegment::getEmbeddingId).toList();
    if (CollectionUtils.isNotEmpty(embeddingIdList)) {
      zhiPuLibraryEmbeddingStore.removeAll(embeddingIdList);
    }
    libraryDocRepository.deleteById(docId);
  }

  public Long createLibraryDocBy(Long libraryId, String objectName, String originalName)
      throws JsonProcessingException {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    String identify =
        String.format(
            "%d%s_%s",
            Instant.now().toEpochMilli(),
            RandomStringUtils.insecure().nextAlphabetic(6),
            originalName);
    Map<String, String> meta = new HashMap<>();
    meta.put("uploader", username);
    LibraryDoc libraryDoc = new LibraryDoc();
    ObjectMapper objectMapper = new ObjectMapper();
    String metaJson = objectMapper.writeValueAsString(meta);
    libraryDoc.setMeta(JSON.valueOf(metaJson));
    libraryDoc.setPath(objectName);
    libraryDoc.setName(originalName);
    libraryDoc.setIdentify(identify);
    libraryDoc.setLibId(libraryId);
    libraryDoc.setStatus(LibraryDocStatusEnum.INDEXING);
    libraryDoc.setEnable(Boolean.TRUE);
    libraryDocRepository.insert(libraryDoc);
    return libraryDocRepository.fetchOneByIdentify(identify).getId();
  }

  @Async
  public void embeddingAndCreateDocSegment(Long libraryId, Long libraryDocId, String objectName) {
    Document document =
        amazonS3DocumentLoader.loadDocument(
            minIoConfig.getDefaultBucket(), objectName, new ApacheTikaDocumentParser());
    List<LibraryDocSegment> libraryDocSegments = new ArrayList<>();
    DocumentByParagraphSplitter documentByParagraphSplitter =
        new DocumentByParagraphSplitter(500, 150);
    documentByParagraphSplitter
        .split(document)
        .forEach(
            textSegment -> {
              Response<Embedding> embed = zhipuEmbeddingModel.embed(textSegment);
              Integer tokenUsage = embed.tokenUsage().totalTokenCount();
              Embedding vector = embed.content();
              textSegment.metadata().put("libraryId", libraryId);
              String embeddingId = zhiPuLibraryEmbeddingStore.add(vector, textSegment);
              LibraryDocSegment libraryDocSegment = new LibraryDocSegment();
              libraryDocSegment.setEmbeddingId(embeddingId);
              libraryDocSegment.setContent(textSegment.text());
              libraryDocSegment.setTokenUsage(tokenUsage);
              libraryDocSegment.setDocId(libraryDocId);
              libraryDocSegments.add(libraryDocSegment);
            });
    libraryDocSegmentDao.insert(libraryDocSegments);
    LibraryDoc libraryDoc = libraryDocRepository.fetchOneById(libraryDocId);
    libraryDoc.setStatus(LibraryDocStatusEnum.SUCCESS);
    libraryDocRepository.update(libraryDoc);
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
