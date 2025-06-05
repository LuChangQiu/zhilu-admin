package com.zl.mjga.component;

import com.zl.mjga.exception.BusinessException;
import com.zl.mjga.repository.PositionRepository;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.output.structured.Description;
import lombok.RequiredArgsConstructor;
import org.jooq.generated.mjga.tables.pojos.Position;
import org.springframework.stereotype.Component;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@Description("和岗位管理有关的操作工具")
@RequiredArgsConstructor
@Component
public class PositionOperatorTool {
  private final PositionRepository positionRepository;

  @Tool(value = "创建岗位")
  void createPosition(@P("岗位名称") String name) {
    Position position = positionRepository.fetchOneByName(name);
    if (position != null) {
      throw new BusinessException("岗位已存在");
    }
    positionRepository.merge(new Position(null, name));
  }

  @Tool(value = "删除岗位")
  void deletePosition(@P("岗位名称") String name) {
    Position position = positionRepository.fetchOneByName(name);
    if (position == null) {
      throw new BusinessException("岗位不存在");
    }
    positionRepository.deleteById(position.getId());
  }
}
