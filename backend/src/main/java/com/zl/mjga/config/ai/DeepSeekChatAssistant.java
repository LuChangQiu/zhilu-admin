package com.zl.mjga.config.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.memory.ChatMemoryAccess;

public interface DeepSeekChatAssistant extends ChatMemoryAccess {
  @SystemMessage("You are a good friend of mine. Answer using slang.")
  TokenStream chat(@MemoryId String memoryId, @UserMessage String userMessage);
}
