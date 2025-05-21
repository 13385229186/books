package com.pyk.bysj.books.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookSimilarity {
  private Long id;
  private Integer bookId;
  private Integer similarBookId;
  private Float score;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public BookSimilarity(Integer bookId, Integer similarBookId, Float score) {
    this.bookId = bookId;
    this.similarBookId = similarBookId;
    this.score = score;
  }

  // 简化DTO转换方法
//  public BookSimilarityDTO toDTO() {
//    return new BookSimilarityDTO(
//            this.book.getId(),
//            this.similarBook.getId(),
//            this.score
//    );
//  }
}