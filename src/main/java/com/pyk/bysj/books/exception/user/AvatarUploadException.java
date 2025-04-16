package com.pyk.bysj.books.exception.user;

import com.pyk.bysj.books.exception.BaseException;

public class AvatarUploadException extends BaseException {
  public AvatarUploadException(String message, Integer code) {
    super(code, message);
  }
}
