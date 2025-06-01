package com.zl.mjga.config.ai;

import com.zl.mjga.dto.urp.UserUpsertDto;
import com.zl.mjga.exception.BusinessException;
import com.zl.mjga.repository.UserRepository;
import com.zl.mjga.service.IdentityAccessService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.output.structured.Description;
import lombok.RequiredArgsConstructor;
import org.jooq.generated.mjga.tables.pojos.User;
import org.springframework.stereotype.Component;

@Description("和用户管理有关的操作工具")
@RequiredArgsConstructor
@Component
public class UserOperatorTool {

  private final IdentityAccessService identityAccessService;
  private final UserRepository userRepository;

  @Tool(value = "创建用户或注册用户")
  void createUser(@P(value = "用户名") String username) {
    User user = userRepository.fetchOneByUsername(username);
    if (user != null) {
      throw new BusinessException("用户已存在");
    }
    identityAccessService.upsertUser(new UserUpsertDto(null, username, username, true));
  }

  @Tool(value = "删除用户")
  void deleteUser(@P(value = "用户名") String username) {
    userRepository.deleteByUsername(username);
  }

  @Tool(value = "编辑/更新/更改用户")
  void updateUser(
      @P(value = "用户名") String username,
      @P(value = "密码", required = false) String password,
      @P(value = "是否开启", required = false) Boolean enable) {
    identityAccessService.upsertUser(new UserUpsertDto(null, username, password, enable));
  }
}
