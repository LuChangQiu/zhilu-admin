package com.zl.mjga.controller;

import com.zl.mjga.service.DeepSeekAiService;
import dev.langchain4j.service.TokenStream;
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

  private final DeepSeekAiService deepSeekAiService;

  @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<String> chat(Principal principal, @RequestBody String userMessage) {
    Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
    TokenStream chat = deepSeekAiService.chat(principal.getName(), userMessage);
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
}
