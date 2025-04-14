package com.pyk.bysj.books.exception.borrow;

import com.pyk.bysj.books.exception.BaseException;

public class BookNotAvailableException extends BaseException {
  public BookNotAvailableException(String message) {
    super(400, message);
  }
}
