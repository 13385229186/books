package com.pyk.bysj.books.enums;

import io.micrometer.common.util.StringUtils;
import lombok.Getter;

@Getter
public enum BookStatus {
  AVAILABLE("可借阅"),
  BORROWED("已借完"),
  MAINTENANCE("维护中");

  private final String description;

  BookStatus(String description) {
    this.description = description;
  }

  /**
   * 从description字符串转换为枚举类型
   * @param value description字符串
   * @return BookStatus
   */
  public static BookStatus fromValue(String value) {
    if (StringUtils.isBlank(value)) {
      throw new IllegalArgumentException("书籍状态不能为空");
    }
    for (BookStatus bookStatus : values()) {
      if (bookStatus.getDescription().equalsIgnoreCase(value)) {
        return bookStatus;
      }
    }
    throw new IllegalArgumentException("无效的书籍状态: " + value);
  }
}
