package com.zl.mjga.repository;

import static org.jooq.generated.mjga.Tables.AI_LLM_CONFIG;

import com.zl.mjga.dto.PageRequestDto;
import org.jooq.Configuration;
import org.jooq.Record;
import org.jooq.Result;
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

  public Result<Record> pageFetchBy(PageRequestDto pageRequestDto) {
    return ctx()
        .select(
            AI_LLM_CONFIG.asterisk(), DSL.count().over().as("total_llm").convertFrom(Long::valueOf))
        .from(AI_LLM_CONFIG)
        .orderBy(pageRequestDto.getSortFields())
        .limit(pageRequestDto.getSize())
        .offset(pageRequestDto.getOffset())
        .fetch();
  }
}
