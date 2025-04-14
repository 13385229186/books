package com.pyk.bysj.books.exception.borrow;

import com.pyk.bysj.books.exception.BaseException;

public class BorrowStatusException extends BaseException {
  public BorrowStatusException(String message) {
    super(400, message);
  }
}
