package com.pyk.bysj.books.model.entity;

import com.pyk.bysj.books.enums.ViolationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViolationRecord {
  private Long id;
  private Integer userId;
  private Integer bookId;
  private Long borrowId;
  @Getter
  private ViolationType violationType;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public ViolationRecord(Integer userId, Integer bookId, Long borrowId, ViolationType violationType) {
    this.userId = userId;
    this.bookId = bookId;
    this.borrowId = borrowId;
    this.violationType = violationType;
  }

}
