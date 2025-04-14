package com.pyk.bysj.books.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookNumber {
  private Integer id;
  private Integer bookId;
  private Integer number;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public BookNumber(Integer bookId, Integer bookNumber) {
    this.bookId = bookId;
    this.number = bookNumber;
  }
}
