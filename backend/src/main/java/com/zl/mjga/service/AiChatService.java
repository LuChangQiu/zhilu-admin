package com.zl.mjga.service;

import com.zl.mjga.config.ai.AiChatAssistant;
import dev.langchain4j.service.TokenStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.generated.default_schema.enums.LlmCodeEnum;
import org.jooq.generated.mjga.tables.pojos.AiLlmConfig;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiChatService {

  private final AiChatAssistant deepSeekChatAssistant;
  private final AiChatAssistant zhiPuChatAssistant;
  private final LlmService llmService;

  public TokenStream chatWithDeepSeek(String sessionIdentifier, String userMessage) {
    return deepSeekChatAssistant.chat(sessionIdentifier, userMessage);
  }

  public TokenStream chatWithZhiPu(String sessionIdentifier, String userMessage) {
    return zhiPuChatAssistant.chat(sessionIdentifier, userMessage);
  }

  public TokenStream chatPrecedenceLlmWith(String sessionIdentifier, String userMessage) {
    AiLlmConfig precedenceLlmBy = llmService.getPrecedenceLlmBy(true);
    LlmCodeEnum code = precedenceLlmBy.getCode();
    return switch (code) {
      case ZHI_PU -> zhiPuChatAssistant.chat(sessionIdentifier, userMessage);
      case DEEP_SEEK -> deepSeekChatAssistant.chat(sessionIdentifier, userMessage);
    };
  }
}
