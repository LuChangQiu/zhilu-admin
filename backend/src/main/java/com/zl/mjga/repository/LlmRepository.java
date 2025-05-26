package com.zl.mjga.repository;

import static org.jooq.generated.mjga.Tables.AI_LLM_CONFIG;
import static org.jooq.impl.DSL.noCondition;

import com.zl.mjga.dto.PageRequestDto;
import com.zl.mjga.dto.ai.LlmQueryDto;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Configuration;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.generated.default_schema.enums.LlmTypeEnum;
import org.jooq.generated.mjga.tables.daos.AiLlmConfigDao;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LlmRepository extends AiLlmConfigDao {

  @Autowired
  public LlmRepository(Configuration configuration) {
    super(configuration);
  }

  public Result<Record> pageFetchBy(PageRequestDto pageRequestDto, LlmQueryDto llmQueryDto) {
    return ctx()
        .select(
            AI_LLM_CONFIG.asterisk(),
                DSL.count().over().as("total_llm").convertFrom(Long::valueOf))
        .from(AI_LLM_CONFIG)
        .where(
            StringUtils.isNotEmpty(llmQueryDto.name())
                ? AI_LLM_CONFIG.NAME.eq(llmQueryDto.name())
                : noCondition())
        .and(
            StringUtils.isNotEmpty(llmQueryDto.type())
                ? AI_LLM_CONFIG.TYPE.eq(LlmTypeEnum.lookupLiteral(llmQueryDto.type()))
                : noCondition())
        .orderBy(pageRequestDto.getSortFields())
        .limit(pageRequestDto.getSize())
        .offset(pageRequestDto.getOffset())
        .fetch();
  }
}
