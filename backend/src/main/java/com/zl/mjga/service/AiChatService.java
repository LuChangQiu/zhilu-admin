package com.zl.mjga.service;

import com.zl.mjga.config.ai.AiChatAssistant;
import dev.langchain4j.service.TokenStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiChatService {

  private final AiChatAssistant deepSeekChatAssistant;
  private final AiChatAssistant zhiPuChatAssistant;

  public TokenStream chatWithDeepSeek(String sessionIdentifier, String userMessage) {
    return deepSeekChatAssistant.chat(sessionIdentifier, userMessage);
  }

  public TokenStream chatWithZhiPu(String sessionIdentifier, String userMessage) {
    return zhiPuChatAssistant.chat(sessionIdentifier, userMessage);
  }
}
