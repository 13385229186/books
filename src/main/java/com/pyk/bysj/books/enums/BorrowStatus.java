package com.pyk.bysj.books.enums;

import io.micrometer.common.util.StringUtils;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum BorrowStatus {
  /**
   * 已申请：用户已提交借阅申请   已申请 ---> 已取消/已过期/借阅中
   */
  APPLIED("已申请"),

  /**
   * 已取消：用户主动取消预约   已申请 ---> 已取消
   */
  CANCELLED("已取消"),

  /**
   * 已过期：超过预约期限未取书，系统自动取消   已申请 ---> 已过期
   */
  EXPIRED("已过期"),

  /**
   * 借阅中：用户已取书，借阅正式开始  已申请 ---> 借阅中 ---> 已归还/已逾期/已丢失
   */
  BORROWED("借阅中"),

  /**
   * 已逾期：超过应还日期未归还   借阅中 ---> 已逾期 ---> 已归还/已丢失
   */
  OVERDUE("已逾期"),

  /**
   * 已归还：图书已归还，借阅流程完成  借阅中/已逾期 ---> 已归还
   */
  RETURNED("已归还"),

  /**
   * 已丢失：图书丢失，用户需赔偿   借阅中/已逾期 ---> 已丢失
   */
  LOST("已丢失");

  private final String description;

  BorrowStatus(String description) {
    this.description = description;
  }

  /**
   * 从description字符串转换为枚举类型
   * @param value description字符串
   * @return BorrowStatus
   */
  public static BorrowStatus fromValue(String value) {
    if (StringUtils.isBlank(value)) {
      throw new IllegalArgumentException("借阅状态不能为空");
    }
    for (BorrowStatus status : values()) {
      if (status.getDescription().equalsIgnoreCase(value)) {
        return status;
      }
    }
    throw new IllegalArgumentException("无效的借阅状态: " + value);
  }
}
