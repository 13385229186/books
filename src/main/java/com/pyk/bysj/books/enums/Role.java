package com.pyk.bysj.books.enums;

import io.micrometer.common.util.StringUtils;
import lombok.Getter;

@Getter
public enum Role {
  ADMIN("管理员"),
  USER("用户");

  private final String description;

  Role(String description) {
    this.description = description;
  }

  /**
   * 从description字符串转换为枚举类型
   * @param value description字符串
   * @return Role
   */
  public static Role fromValue(String value) {
    if (StringUtils.isBlank(value)) {
      return null;
//      throw new IllegalArgumentException("角色类型不能为空");
    }
    for (Role role : values()) {
      if (role.getDescription().equalsIgnoreCase(value)) {
        return role;
      }
    }
    throw new IllegalArgumentException("无效的角色类型: " + value);
  }
}
