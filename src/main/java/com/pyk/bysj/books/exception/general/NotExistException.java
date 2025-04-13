package com.pyk.bysj.books.exception.general;

import com.pyk.bysj.books.exception.BaseException;

public class NotExistException extends BaseException {
  public NotExistException(String message) {
    super(400, message);
  }
  public NotExistException(Integer code, String message) {
    super(code, message);
  }
}
