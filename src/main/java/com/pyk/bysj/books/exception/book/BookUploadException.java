package com.pyk.bysj.books.exception.book;

import com.pyk.bysj.books.exception.BaseException;

public class BookUploadException extends BaseException {

  public BookUploadException(String message, Integer code) {
    super(code, message);
  }
}
