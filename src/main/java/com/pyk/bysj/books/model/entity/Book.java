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
  private String cover;
  private String title;
  private String author;
  private String press;
  private Integer categoryId;
  private String intro;
  @Getter
  private BookStatus status;
  private String ebook;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Book(String isbn, String cover, String title, String author, String press, Integer categoryId, String intro, BookStatus status, String ebook) {
    this.isbn = isbn;
    this.cover = cover;
    this.title = title;
    this.author = author;
    this.press = press;
    this.categoryId = categoryId;
    this.intro = intro;
    this.status = status;
    this.ebook = ebook;
  }

}
