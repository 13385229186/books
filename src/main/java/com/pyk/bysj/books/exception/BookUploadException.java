package com.pyk.bysj.books.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class BookUploadException extends RuntimeException {
  private final Integer status;

  public BookUploadException(String message, Integer status) {
    super(message);
    this.status = status;
  }
}
