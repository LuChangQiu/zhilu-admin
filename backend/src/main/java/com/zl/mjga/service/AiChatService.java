package com.zl.mjga.service;

import com.zl.mjga.config.ai.AiChatAssistant;
import com.zl.mjga.config.ai.SystemToolAssistant;
import com.zl.mjga.dto.ai.ChatDto;
import com.zl.mjga.exception.BusinessException;
import dev.langchain4j.service.TokenStream;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.generated.mjga.enums.LlmCodeEnum;
import org.jooq.generated.mjga.tables.pojos.AiLlmConfig;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiChatService {

  private final AiChatAssistant deepSeekChatAssistant;
  private final AiChatAssistant zhiPuChatAssistant;
  private final SystemToolAssistant zhiPuToolAssistant;
  private final SystemToolAssistant deepSeekToolAssistant;
  private final LlmService llmService;

  public TokenStream chatWithDeepSeek(String sessionIdentifier, String userMessage) {
    return deepSeekChatAssistant.chat(sessionIdentifier, userMessage);
  }

  public TokenStream chatWithZhiPu(String sessionIdentifier, String userMessage) {
    return zhiPuChatAssistant.chat(sessionIdentifier, userMessage);
  }

  public TokenStream actionPrecedenceExecuteWith(String sessionIdentifier, String userMessage) {
    LlmCodeEnum code = getPrecedenceLlmCode();
    return switch (code) {
      case ZHI_PU -> zhiPuToolAssistant.ask(sessionIdentifier, userMessage);
      case DEEP_SEEK -> deepSeekToolAssistant.ask(sessionIdentifier, userMessage);
      default -> throw new BusinessException(String.format("无效的模型代码 %s", code));
    };
  }

  public TokenStream chat(String sessionIdentifier, ChatDto chatDto) {
    return switch (chatDto.mode()) {
      case NORMAL -> chatWithPrecedenceLlm(sessionIdentifier, chatDto);
      case WITH_LIBRARY -> chatWithLibrary(chatDto.libraryId(), chatDto);
    };
  }

  public TokenStream chatWithLibrary(Long libraryId, ChatDto chatDto) {
    return zhiPuChatAssistant.chat(String.valueOf(libraryId), chatDto.message());
  }

  public TokenStream chatWithPrecedenceLlm(String sessionIdentifier, ChatDto chatDto) {
    LlmCodeEnum code = getPrecedenceLlmCode();
    String userMessage = chatDto.message();
    return switch (code) {
      case ZHI_PU -> zhiPuChatAssistant.chat(sessionIdentifier, userMessage);
      case DEEP_SEEK -> deepSeekChatAssistant.chat(sessionIdentifier, userMessage);
      default -> throw new BusinessException(String.format("无效的模型代码 %s", code));
    };
  }

  private LlmCodeEnum getPrecedenceLlmCode() {
    Optional<AiLlmConfig> precedenceLlmBy = llmService.getPrecedenceChatLlmBy(true);
    AiLlmConfig aiLlmConfig = precedenceLlmBy.orElseThrow(() -> new BusinessException("没有开启的大模型"));
    return aiLlmConfig.getCode();
  }

  public void evictChatMemory(String sessionIdentifier) {
    deepSeekChatAssistant.evictChatMemory(sessionIdentifier);
    zhiPuChatAssistant.evictChatMemory(sessionIdentifier);
    zhiPuToolAssistant.evictChatMemory(sessionIdentifier);
    deepSeekToolAssistant.evictChatMemory(sessionIdentifier);
  }
}
