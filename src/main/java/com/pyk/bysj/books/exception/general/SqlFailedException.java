package com.pyk.bysj.books.exception.general;

import com.pyk.bysj.books.exception.BaseException;

public class SqlFailedException extends BaseException {
  public SqlFailedException(String message) {
    super(500, message);
  }
}
