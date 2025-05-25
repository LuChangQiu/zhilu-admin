package com.zl.mjga.controller;

import com.zl.mjga.dto.PageRequestDto;
import com.zl.mjga.dto.PageResponseDto;
import com.zl.mjga.dto.ai.LlmQueryDto;
import com.zl.mjga.dto.ai.LlmVm;
import com.zl.mjga.exception.BusinessException;
import com.zl.mjga.service.AiChatService;
import com.zl.mjga.service.EmbeddingService;
import com.zl.mjga.service.LlmService;
import dev.langchain4j.service.TokenStream;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.generated.mjga.enums.LlmCodeEnum;
import org.jooq.generated.mjga.tables.pojos.AiLlmConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
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
  private final EmbeddingService embeddingService;

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

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_LLM_CONFIG_PERMISSION)")
  @PutMapping(value = "/llm")
  public void updateLlm(@RequestBody @Valid LlmVm llmVm) {
    llmService.update(llmVm);
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).READ_LLM_CONFIG_PERMISSION)")
  @GetMapping("/llm/page-query")
  @ResponseStatus(HttpStatus.OK)
  public PageResponseDto<List<LlmVm>> pageQueryLlm(
      @ModelAttribute PageRequestDto pageRequestDto, @ModelAttribute LlmQueryDto llmQueryDto) {
    return llmService.pageQueryLlm(pageRequestDto, llmQueryDto);
  }

  @PostMapping("/action/chat")
  public Map<String, Object> actionChat(@RequestBody String message) {
    AiLlmConfig aiLlmConfig = llmService.loadConfig(LlmCodeEnum.ZHI_PU);
    if (!aiLlmConfig.getEnable()) {
      throw new BusinessException("命令模型未启用，请开启后再试。");
    }
    return embeddingService.searchAction(message);
  }
}
