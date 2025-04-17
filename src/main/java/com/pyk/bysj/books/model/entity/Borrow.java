package com.pyk.bysj.books.model.entity;

import com.pyk.bysj.books.enums.BorrowStatus;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Borrow {
  private Long id;
  private Integer userId;
  private Integer bookId;
  private Integer borrowDays;
  private LocalDateTime borrowTime;
  private LocalDateTime dueTime;
  private LocalDateTime returnTime;
  @Getter
  private BorrowStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Borrow(Integer userId, Integer bookId, Integer borrowDays) {
    this.userId = userId;
    this.bookId = bookId;
    this.borrowDays = borrowDays;
  }

  public Borrow(Long id, Integer userId, Integer bookId, Integer borrowDays, LocalDateTime borrowTime, LocalDateTime dueTime, LocalDateTime returnTime, BorrowStatus status, LocalDateTime createdAt) {
    this.id = id;
    this.userId = userId;
    this.bookId = bookId;
    this.borrowDays = borrowDays;
    this.borrowTime = borrowTime;
    this.dueTime = dueTime;
    this.returnTime = returnTime;
    this.status = status;
    this.createdAt = createdAt;
  }

}
