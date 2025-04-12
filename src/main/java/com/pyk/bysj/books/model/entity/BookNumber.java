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
  private String isbn;
  private Integer number;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public BookNumber(String isbn, Integer bookNumber) {
    this.isbn = isbn;
    this.number = bookNumber;
  }
}
