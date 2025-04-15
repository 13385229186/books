package com.pyk.bysj.books.enums;

import io.micrometer.common.util.StringUtils;
import lombok.Getter;

@Getter
public enum ActionType {
  BROWSE("浏览"),
  BORROW("借阅"),
  RATE("评价");

  private final String description;

  ActionType(String description) {
    this.description = description;
  }

  /**
   * 从description字符串转换为枚举类型
   * @param value description字符串
   * @return ActionType
   */
  public static ActionType fromValue(String value) {
    if (StringUtils.isBlank(value)) {
      throw new IllegalArgumentException("行为类型不能为空");
    }
    for (ActionType actionType : values()) {
      if (actionType.getDescription().equalsIgnoreCase(value)) {
        return actionType;
      }
    }
    throw new IllegalArgumentException("无效的行为类型: " + value);
  }
}
