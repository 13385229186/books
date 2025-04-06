package com.pyk.bysj.books.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
  private Integer id;
  private String name;
  private Integer parentId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private List<Category> children;
}