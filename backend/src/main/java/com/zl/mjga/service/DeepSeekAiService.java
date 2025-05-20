package com.zl.mjga.service;

import com.zl.mjga.config.ai.DeepSeekChatAssistant;
import dev.langchain4j.service.TokenStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeepSeekAiService {

  private final DeepSeekChatAssistant deepSeekChatAssistant;

  public TokenStream chat(String sessionIdentifier, String userMessage) {
    return deepSeekChatAssistant.chat(sessionIdentifier, userMessage);
  }
}
