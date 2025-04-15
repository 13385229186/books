package com.pyk.bysj.books.enums;

import io.micrometer.common.util.StringUtils;
import lombok.Getter;

@Getter
public enum UserStatus {
  ACTIVE("正常"),
  DISABLED("封控"),
  LOCKED("锁定"),
  DELETED("注销");

  private final String description;

  UserStatus(String description) {
    this.description = description;
  }

  /**
   * 从description字符串转换为枚举类型
   * @param value description字符串
   * @return UserStatus
   */
  public static UserStatus fromValue(String value) {
    if (StringUtils.isBlank(value)) {
      throw new IllegalArgumentException("用户状态不能为空");
    }
    for (UserStatus userStatus : values()) {
      if (userStatus.getDescription().equalsIgnoreCase(value)) {
        return userStatus;
      }
    }
    throw new IllegalArgumentException("无效的用户状态: " + value);
  }
}
