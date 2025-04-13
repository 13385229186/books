package com.pyk.bysj.books.exception.library;

import com.pyk.bysj.books.exception.BaseException;
import lombok.Builder;

public class CreditException extends BaseException {
  public CreditException(Integer code, String message) {
    super(code, message);
  }
  public CreditException(String message) {
    super(400, message);
  }
}
