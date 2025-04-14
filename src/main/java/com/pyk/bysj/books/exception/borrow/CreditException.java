package com.pyk.bysj.books.exception.borrow;

import com.pyk.bysj.books.exception.BaseException;

public class CreditException extends BaseException {
  public CreditException(Integer code, String message) {
    super(code, message);
  }
  public CreditException(String message) {
    super(400, message);
  }
}
