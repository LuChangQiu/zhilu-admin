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
import org.jooq.generated.mjga.tables.daos.LibraryDocSegmentDao;
import org.jooq.generated.mjga.tables.pojos.LibraryDoc;
import org.jooq.generated.mjga.tables.pojos.LibraryDocSegment;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Service
@Slf4j
public class RagService {

  private final EmbeddingModel zhipuEmbeddingModel;

  private final EmbeddingStore<TextSegment> zhiPuEmbeddingStore;

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
      zhiPuEmbeddingStore.removeAll(embeddingIdList);
    }
    libraryDocRepository.deleteById(docId);
  }

  @Transactional(rollbackFor = Throwable.class)
  public void ingestDocumentBy(Long libraryId, String objectName, String originalName)
      throws Exception {
    Document document =
        amazonS3DocumentLoader.loadDocument(
            minIoConfig.getDefaultBucket(), objectName, new ApacheTikaDocumentParser());
    ArrayList<String> embeddingIds = new ArrayList<>();
    try {
      Long libraryDocId = createLibraryDoc(objectName, originalName, document.metadata().toMap());
      DocumentByParagraphSplitter documentByParagraphSplitter =
          new DocumentByParagraphSplitter(1000, 200);
      documentByParagraphSplitter
          .split(document)
          .forEach(
              textSegment -> {
                Metadata metadata = textSegment.metadata();
                metadata.put("libraryId", libraryId);
                Response<Embedding> embed = zhipuEmbeddingModel.embed(textSegment);
                Integer tokenUsage = embed.tokenUsage().totalTokenCount();
                Embedding vector = embed.content();
                String embeddingId = zhiPuEmbeddingStore.add(vector, textSegment);
                embeddingIds.add(embeddingId);
                createLibraryDocSegment(textSegment, libraryDocId, tokenUsage, embeddingId);
              });
    } catch (Exception e) {
      log.error(
          "文档采集失败。libraryId {} objectName {} originalName {}",
          libraryId,
          objectName,
          originalName,
          e);
      if (CollectionUtils.isNotEmpty(embeddingIds)) {
        zhiPuEmbeddingStore.removeAll(embeddingIds);
      }
      throw e;
    }
  }

  private void createLibraryDocSegment(
      TextSegment textSegment, Long libraryDocId, Integer tokenUsage, String embeddingId) {
    LibraryDocSegment libraryDocSegment = new LibraryDocSegment();
    libraryDocSegment.setDocId(libraryDocId);
    libraryDocSegment.setContent(textSegment.text());
    libraryDocSegment.setTokenUsage(tokenUsage);
    libraryDocSegment.setEmbeddingId(embeddingId);
    libraryDocSegmentDao.insert();
  }

  private Long createLibraryDoc(String objectName, String originalName, Map meta)
      throws JsonProcessingException {
    String identify =
        String.format(
            "%d%s_%s",
            Instant.now().toEpochMilli(),
            RandomStringUtils.insecure().nextAlphabetic(6),
            originalName);
    LibraryDoc libraryDoc = new LibraryDoc();
    ObjectMapper objectMapper = new ObjectMapper();
    String metaJson = objectMapper.writeValueAsString(meta);
    libraryDoc.setMeta(JSON.valueOf(metaJson));
    libraryDoc.setPath(objectName);
    libraryDoc.setName(originalName);
    libraryDoc.setIdentify(identify);
    libraryDocRepository.insert(libraryDoc);
    return libraryDocRepository.fetchOneByIdentify(identify).getId();
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
