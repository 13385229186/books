package com.pyk.bysj.books.model.dto;

import com.pyk.bysj.books.enums.BorrowStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BorrowListDTO {
  // 用户信息
  private String name;
  private String phone;

  // 书籍信息
  private String title; // 书名

  // 借阅信息
  private Integer id;
  private Integer userId;
  private Integer bookId;
  private Integer borrowDays;
  private LocalDateTime borrowTime;
  private LocalDateTime dueTime;
  private LocalDateTime returnTime;
  private String status;

  public BorrowStatus getStatus() {
    return BorrowStatus.fromValue(status);
  }

}
