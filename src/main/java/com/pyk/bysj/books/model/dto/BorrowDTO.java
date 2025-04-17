package com.pyk.bysj.books.model.dto;

import com.pyk.bysj.books.enums.BorrowStatus;
import com.pyk.bysj.books.model.entity.Borrow;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BorrowDTO {
  // 用户信息
  private String name;
  private String phone;

  // 书籍信息
  private String title; // 书名

  // 借阅信息
  private Long id;
  private Integer userId;
  private Integer bookId;
  private Integer borrowDays;
  private LocalDateTime borrowTime;
  private LocalDateTime dueTime;
  private LocalDateTime returnTime;
  private BorrowStatus status;
  private LocalDateTime createdAt;

  /**
   * 转为Borrow对象
   * @return Borrow
   */
  public Borrow toBorrow(){
    return new Borrow(id, userId, bookId, borrowDays, borrowTime, dueTime, returnTime, status, createdAt);
  }

}
