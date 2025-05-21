package com.pyk.bysj.books.exception.user;

import com.pyk.bysj.books.exception.BaseException;

public class UserStatusException extends BaseException {
  public UserStatusException(String message) {
    super(4003, message);
  }
}
