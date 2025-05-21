package com.pyk.bysj.books.enums;

import io.micrometer.common.util.StringUtils;
import lombok.Getter;

/**
 * 用户行为类型枚举
 */
@Getter
public enum BehaviorType {
  /**
   * 查看书籍详情
   */
  VIEW(1.0f, "查看详情"),

  /**
   * 在线阅读电子书
   */
  READ_ONLINE(2.0f, "在线阅读"),

  /**
   * 下载电子书
   */
  DOWNLOAD(3.0f, "下载电子书"),

  /**
   * 借阅实体书
   */
  BORROW(5.0f, "借阅实体书");

  /**
   * -- GETTER --
   *  获取行为权重
   *
   * @return 权重值
   */
  private final float weight;
  /**
   * -- GETTER --
   *  获取行为描述
   *
   * @return 描述文本
   */
  private final String description;

  BehaviorType(float weight, String description) {
    this.weight = weight;
    this.description = description;
  }

  /**
   * 从description字符串转换为枚举类型
   * @param value description字符串
   * @return BehaviorType
   */
  public static BehaviorType fromValue(String value) {
    if (StringUtils.isBlank(value)) {
      throw new IllegalArgumentException("行为类型不能为空");
    }
    for (BehaviorType behaviorType : values()) {
      if (behaviorType.getDescription().equalsIgnoreCase(value)) {
        return behaviorType;
      }
    }
    throw new IllegalArgumentException("无效的行为类型: " + value);
  }

  /**
   * 检查给定的名称是否是有效的行为类型
   * @param name 要检查的名称
   * @return 如果有效返回true，否则返回false
   */
  public static boolean isValid(String name) {
    for (BehaviorType type : values()) {
      if (type.name().equalsIgnoreCase(name)) {
        return true;
      }
    }
    return false;
  }
}