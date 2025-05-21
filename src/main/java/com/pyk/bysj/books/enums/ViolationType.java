package com.pyk.bysj.books.enums;

import io.micrometer.common.util.StringUtils;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;

@Getter
public enum ViolationType {
  /**
   * 逾期未还，扣20分
   */
  OVERDUE("逾期未还"),
  /**
   * 书籍丢失，扣40分
   */
  LOST("书籍丢失"),
  /**
   * 申请过期，扣10分
   */
  EXPIRED("申请过期");

  private final String description;

  ViolationType(String description) {
    this.description = description;
  }

  /**
   * 从description字符串转换为枚举类型
   * @param value description字符串
   * @return ViolationType
   */
  public static ViolationType fromValue(String value) {
    if (StringUtils.isBlank(value)) {
      throw new IllegalArgumentException("违规行为不能为空");
    }
    for (ViolationType violationType : values()) {
      if (violationType.getDescription().equalsIgnoreCase(value)) {
        return violationType;
      }
    }
    throw new IllegalArgumentException("无效的违规行为: " + value);
  }

  public static boolean canConvertByName(String name) {
    try {
      ViolationType.valueOf(name);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

}
