package com.pyk.bysj.books.exception.security;

import com.pyk.bysj.books.exception.BaseException;
import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {
  public JwtAuthenticationException(String msg) {
    super(msg);
  }
}
