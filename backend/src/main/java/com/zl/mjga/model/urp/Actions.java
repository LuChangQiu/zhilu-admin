package com.zl.mjga.model.urp;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Actions {
  CREATE_USER("CREATE_USER", "创建新用户");
  public static final String INDEX_KEY = "action";
  private final String code;
  private final String content;
}
