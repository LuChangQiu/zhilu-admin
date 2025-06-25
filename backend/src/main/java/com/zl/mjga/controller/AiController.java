package com.zl.mjga.controller;

import com.zl.mjga.dto.PageRequestDto;
import com.zl.mjga.dto.PageResponseDto;
import com.zl.mjga.dto.ai.LlmQueryDto;
import com.zl.mjga.dto.ai.LlmVm;
import com.zl.mjga.exception.BusinessException;
import com.zl.mjga.repository.*;
import com.zl.mjga.service.AiChatService;
import com.zl.mjga.service.EmbeddingService;
import com.zl.mjga.service.LlmService;
import com.zl.mjga.service.UploadService;
import dev.langchain4j.service.TokenStream;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.generated.mjga.enums.LlmCodeEnum;
import org.jooq.generated.mjga.tables.pojos.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
  private final UserRepository userRepository;
  private final DepartmentRepository departmentRepository;
  private final PositionRepository positionRepository;
  private final RoleRepository repository;
  private final PermissionRepository permissionRepository;
  private final RoleRepository roleRepository;
  private final UploadService uploadService;

  @PostMapping(value = "/action/execute", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<String> actionExecute(Principal principal, @RequestBody String userMessage) {
    Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
    TokenStream chat = aiChatService.actionPrecedenceExecuteWith(principal.getName(), userMessage);
    chat.onPartialResponse(
            (text) -> {
              log.debug("ai action partialResponse: {}", text);
              sink.tryEmitNext(
                  StringUtils.isNotEmpty(text) ? text.replace(" ", "␣").replace("\t", "⇥") : "");
            })
        .onToolExecuted(
            toolExecution -> log.debug("当前请求 {} 成功执行函数调用: {}", userMessage, toolExecution))
        .onCompleteResponse(
            r -> {
              log.debug("ai action completeResponse: {}", r);
              sink.tryEmitComplete();
              sink.emitComplete(Sinks.EmitFailureHandler.FAIL_FAST);
            })
        .onError(
            (e) -> {
              sink.tryEmitError(e);
              sink.tryEmitComplete();
              sink.emitComplete(Sinks.EmitFailureHandler.FAIL_FAST);
            })
        .start();
    return sink.asFlux().timeout(Duration.ofSeconds(120));
  }

  @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<String> chat(Principal principal, @RequestBody String userMessage) {
    Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
    TokenStream chat = aiChatService.chatPrecedenceLlmWith(principal.getName(), userMessage);
    chat.onPartialResponse(
            text ->
                sink.tryEmitNext(
                    StringUtils.isNotEmpty(text) ? text.replace(" ", "␣").replace("\t", "⇥") : ""))
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

  @PostMapping("/action/search")
  public Map<String, String> searchAction(@RequestBody String message) {
    AiLlmConfig aiLlmConfig = llmService.loadConfig(LlmCodeEnum.ZHI_PU);
    if (!aiLlmConfig.getEnable()) {
      throw new BusinessException("命令模型未启用，请开启后再试。");
    }
    return embeddingService.searchAction(message);
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_USER_ROLE_PERMISSION)")
  @DeleteMapping("/action/user")
  void deleteUser(@RequestParam String username, Principal principal) {
    if (StringUtils.equals(username, principal.getName())) {
      throw new BusinessException("不能删除当前登录用户");
    }
    User fetched = userRepository.fetchOneByUsername(username);
    if (fetched == null) {
      throw new BusinessException("该用户不存在");
    }
    userRepository.deleteByUsername(username);
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_DEPARTMENT_PERMISSION)")
  @DeleteMapping("/action/department")
  void deleteDepartment(@RequestParam String name) {
    Department department = departmentRepository.fetchOneByName(name);
    if (department == null) {
      throw new BusinessException("该部门不存在");
    }
    departmentRepository.deleteByName(name);
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_POSITION_PERMISSION)")
  @DeleteMapping("/action/position")
  void deletePosition(@RequestParam String name) {
    Position position = positionRepository.fetchOneByName(name);
    if (position == null) {
      throw new BusinessException("该岗位不存在");
    }
    positionRepository.deleteById(position.getId());
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_USER_ROLE_PERMISSION)")
  @DeleteMapping("/action/role")
  void deleteRole(@RequestParam String name) {
    Role role = roleRepository.fetchOneByName(name);
    if (role == null) {
      throw new BusinessException("该角色不存在");
    }
    roleRepository.deleteById(role.getId());
  }

  @PreAuthorize("hasAuthority(T(com.zl.mjga.model.urp.EPermission).WRITE_USER_ROLE_PERMISSION)")
  @DeleteMapping("/action/permission")
  void deletePermission(@RequestParam String name) {
    Permission permission = permissionRepository.fetchOneByName(name);
    if (permission == null) {
      throw new BusinessException("该权限不存在");
    }
    permissionRepository.deleteById(permission.getId());
  }

  @PostMapping("/chat/refresh")
  void createNewConversation(Principal principal) {
    aiChatService.evictChatMemory(principal.getName());
  }

  @PostMapping(
      value = "/library/upload",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.TEXT_PLAIN_VALUE)
  public String uploadLibraryFile(@RequestPart("file") MultipartFile multipartFile)
      throws Exception {
    String objectName = uploadService.uploadLibraryFile(multipartFile);
    embeddingService.ingestDocument(objectName);
    return objectName;
  }
}
