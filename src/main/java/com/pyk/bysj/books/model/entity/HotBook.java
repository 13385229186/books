package com.pyk.bysj.books.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotBook {
  private Integer id;
  private Integer bookId;
  private Float heatScore;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public HotBook(Integer bookId, Float heatScore) {
    this.bookId = bookId;
    this.heatScore = heatScore;
  }
}