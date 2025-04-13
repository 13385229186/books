package com.pyk.bysj.books.exception.general;

import com.pyk.bysj.books.exception.BaseException;

public class OperationFailedException extends BaseException {
  public OperationFailedException(String message) {
    super(400, message);
  }
  public OperationFailedException(Integer code, String message) {
    super(code, message);
  }
}
