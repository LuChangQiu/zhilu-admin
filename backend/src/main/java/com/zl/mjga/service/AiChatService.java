package com.zl.mjga.service;

import com.zl.mjga.config.ai.AiChatAssistant;
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
  private final LlmService llmService;

  public TokenStream chatWithDeepSeek(String sessionIdentifier, String userMessage) {
    return deepSeekChatAssistant.chat(sessionIdentifier, userMessage);
  }

  public TokenStream chatWithZhiPu(String sessionIdentifier, String userMessage) {
    return zhiPuChatAssistant.chat(sessionIdentifier, userMessage);
  }

  public TokenStream chatPrecedenceLlmWith(String sessionIdentifier, String userMessage) {
    Optional<AiLlmConfig> precedenceLlmBy = llmService.getPrecedenceChatLlmBy(true);
    AiLlmConfig aiLlmConfig = precedenceLlmBy.orElseThrow(() -> new BusinessException("没有开启的大模型"));
    LlmCodeEnum code = aiLlmConfig.getCode();
    return switch (code) {
      case ZHI_PU -> zhiPuChatAssistant.chat(sessionIdentifier, userMessage);
      case DEEP_SEEK -> deepSeekChatAssistant.chat(sessionIdentifier, userMessage);
      default -> throw new BusinessException(String.format("无效的模型代码 %s", code));
    };
  }
}
