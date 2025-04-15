package com.pyk.bysj.books.model.dto;

import com.pyk.bysj.books.enums.BookStatus;
import com.pyk.bysj.books.model.entity.Book;
import com.pyk.bysj.books.model.entity.Login;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDTO {
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

  private Integer bookNumber = 0;

  @Valid
  private PageParam pageParam = new PageParam();

  public Book toEntity(){
    return new Book(this.id, this.isbn, this.cover, this.title, this.author, this.press, this.categoryId, this.intro, this.status, this.ebook);
  }
}
