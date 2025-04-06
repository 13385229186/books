package com.pyk.bysj.books.model.dto;

import com.pyk.bysj.books.enums.BookStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDTO {
  private String isbn;
  private String title;
  private String author;
  private String press;
  private Integer categoryId;
  @Getter
  private BookStatus status;

  /**
   * 当前页数
   */
  private Integer current;

  /**
   * 每页数据条数
   */
  private Integer pageSize;
}
