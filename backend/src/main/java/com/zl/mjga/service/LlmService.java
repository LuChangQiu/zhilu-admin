package com.zl.mjga.service;

import static org.jooq.generated.mjga.Tables.AI_LLM_CONFIG;

import com.zl.mjga.dto.PageRequestDto;
import com.zl.mjga.dto.PageResponseDto;
import com.zl.mjga.dto.ai.LlmQueryDto;
import com.zl.mjga.dto.ai.LlmVm;
import com.zl.mjga.repository.LlmRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.generated.default_schema.enums.LlmTypeEnum;
import org.jooq.generated.mjga.enums.LlmCodeEnum;
import org.jooq.generated.mjga.tables.pojos.AiLlmConfig;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LlmService {

  private final LlmRepository llmRepository;

  public AiLlmConfig loadConfig(LlmCodeEnum llmCodeEnum) {
    return llmRepository.fetchOneByCode(llmCodeEnum);
  }

  public Optional<AiLlmConfig> getPrecedenceChatLlmBy(Boolean enable) {
    List<AiLlmConfig> aiLlmConfigs = llmRepository.fetchByEnable(enable);
    return aiLlmConfigs.stream()
        .filter(aiLlmConfig -> LlmTypeEnum.CHAT.equals(aiLlmConfig.getType()))
            .max(Comparator.comparingInt(AiLlmConfig::getPriority));
  }

  public PageResponseDto<List<LlmVm>> pageQueryLlm(
      PageRequestDto pageRequestDto, LlmQueryDto llmQueryDto) {
    Result<Record> records = llmRepository.pageFetchBy(pageRequestDto, llmQueryDto);
    if (records.isEmpty()) {
      return PageResponseDto.empty();
    }
    List<LlmVm> llmVms =
        records.map(
            (record) -> {
              LlmVm into = record.into(LlmVm.class);
              into.setType(record.get(AI_LLM_CONFIG.TYPE).getLiteral());
              return into;
            });
    Long totalLlm = records.get(0).getValue("total_llm", Long.class);
    return new PageResponseDto<>(totalLlm, llmVms);
  }

  public void update(LlmVm llmVm) {
    AiLlmConfig aiLlmConfig = new AiLlmConfig();
    BeanUtils.copyProperties(llmVm, aiLlmConfig);
    AiLlmConfig byId = llmRepository.findById(llmVm.getId());
    aiLlmConfig.setCode(Objects.requireNonNull(byId).getCode());
    aiLlmConfig.setType(LlmTypeEnum.lookupLiteral(llmVm.getType()));
    llmRepository.merge(aiLlmConfig);
  }
}
