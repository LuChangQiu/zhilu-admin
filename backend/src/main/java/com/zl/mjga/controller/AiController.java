package com.zl.mjga.controller;

import com.zl.mjga.dto.ai.LlmUpdateDto;
import com.zl.mjga.service.AiChatService;
import com.zl.mjga.service.LlmService;
import dev.langchain4j.service.TokenStream;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Slf4j
public class AiController {

  private final AiChatService aiChatService;
  private final LlmService llmService;

  @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<String> chat(Principal principal, @RequestBody String userMessage) {
    Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
    TokenStream chat = aiChatService.chatPrecedenceLlmWith(principal.getName(), userMessage);
    chat.onPartialResponse(text -> sink.tryEmitNext(text.replace(" ", "␣").replace("\t", "⇥")))
        .onCompleteResponse(
            r -> {
              sink.tryEmitComplete();
              sink.emitComplete(Sinks.EmitFailureHandler.FAIL_FAST);
            })
        .onError(sink::tryEmitError)
        .start();
    return sink.asFlux().timeout(Duration.ofSeconds(120));
  }

  @PutMapping(value = "/llm")
  public void updateLlm(@RequestBody @Valid LlmUpdateDto llmUpdateDto) {
    llmService.update(llmUpdateDto);
  }
}
