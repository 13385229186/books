package com.pyk.bysj.books.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Borrow {
  private Long id;
  private Integer userId;
  private String isbn;
  private LocalDateTime borrowTime;
  private LocalDateTime dueTime;
  private LocalDateTime returnTime;
  private Double fineAmount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
