package com.zl.mjga.service;

import com.zl.mjga.dto.ai.LlmUpdateDto;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.generated.default_schema.enums.LlmCodeEnum;
import org.jooq.generated.mjga.tables.daos.AiLlmConfigDao;
import org.jooq.generated.mjga.tables.pojos.AiLlmConfig;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LlmService {

  private final AiLlmConfigDao aiLlmConfigDao;

  public AiLlmConfig loadConfig(LlmCodeEnum llmCodeEnum) {
    return aiLlmConfigDao.fetchOneByCode(llmCodeEnum);
  }

  public AiLlmConfig getPrecedenceLlmBy(Boolean enable) {
    List<AiLlmConfig> aiLlmConfigs = aiLlmConfigDao.fetchByEnable(enable);
    //noinspection OptionalGetWithoutIsPresent
    return aiLlmConfigs.stream()
        .max((o1, o2) -> o2.getPriority().compareTo(o1.getPriority()))
        .get();
  }

  public void update(LlmUpdateDto llmUpdateDto) {
    AiLlmConfig aiLlmConfig = new AiLlmConfig();
    BeanUtils.copyProperties(llmUpdateDto, aiLlmConfig);
    AiLlmConfig byId = aiLlmConfigDao.findById(llmUpdateDto.getId());
    aiLlmConfig.setCode(Objects.requireNonNull(byId).getCode());
    aiLlmConfigDao.merge(aiLlmConfig);
  }
}
