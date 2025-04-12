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
  private Integer book_id;
  private Integer number;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public BookNumber(Integer book_id, Integer bookNumber) {
    this.book_id = book_id;
    this.number = bookNumber;
  }
}
