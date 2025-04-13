package com.pyk.bysj.books.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {
  private final int code;

  public BaseException(int code, String message) {
    super(message);
    this.code = code;
  }

}
