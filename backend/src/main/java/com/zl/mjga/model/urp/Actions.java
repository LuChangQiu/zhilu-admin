package com.zl.mjga.model.urp;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Actions {
  CREATE_USER("CREATE_USER", "创建用户"),
  CREATE_DEPARTMENT("CREATE_DEPARTMENT", "创建部门");
  public static final String INDEX_KEY = "action";
  private final String code;
  private final String content;
}
