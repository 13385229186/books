package com.pyk.bysj.books.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotBooks {
  private Integer hotId;
  private Integer categoryId;
  private String isbn;
  private Float heatScore;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}