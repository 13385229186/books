package com.pyk.bysj.books.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
  private Integer id;
  private String name;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Category(String name) {
    this.name = name;
  }
}