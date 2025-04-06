package com.pyk.bysj.books.model.entity;

import com.pyk.bysj.books.enums.BookStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
  private String isbn;
  private String title;
  private String author;
  private String press;
  private Integer categoryId;
  @Getter
  private BookStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

}
