package com.zl.mjga.controller;

import com.zl.mjga.service.DeepSeekAiService;
import dev.langchain4j.service.TokenStream;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Slf4j
public class AiController {

  private final DeepSeekAiService deepSeekAiService;

  @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<String> chat(@RequestBody String userMessage) {
    Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
    TokenStream chat = deepSeekAiService.chat("123", userMessage);
    chat.onPartialResponse(sink::tryEmitNext)
        .onCompleteResponse(
            r -> {
              sink.tryEmitNext("[DONE]");
              sink.tryEmitComplete();
            })
        .onError(sink::tryEmitError)
        .start();

    return sink.asFlux()
        .timeout(Duration.ofSeconds(60))
        .onErrorResume(e -> Flux.just("Timeout occurred"));
  }
}
