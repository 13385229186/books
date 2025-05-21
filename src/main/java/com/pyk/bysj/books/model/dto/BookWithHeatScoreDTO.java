package com.pyk.bysj.books.model.dto;

import com.pyk.bysj.books.enums.BookStatus;
import com.pyk.bysj.books.model.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookWithHeatScoreDTO {
  private Integer id;
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
  private Float heatScore;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public BookWithHeatScoreDTO(Book book, Float heatScore) {
    this.id = book.getId();
    this.isbn = book.getIsbn();
    this.cover = book.getCover();
    this.title = book.getTitle();
    this.author = book.getAuthor();
    this.press = book.getPress();
    this.categoryId = book.getCategoryId();
    this.intro = book.getIntro();
    this.status = book.getStatus();
    this.ebook = book.getEbook();
    this.heatScore = heatScore;
    this.createdAt = book.getCreatedAt();
    this.updatedAt = book.getUpdatedAt();
  }

}

