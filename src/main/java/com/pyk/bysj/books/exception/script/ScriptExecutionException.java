package com.pyk.bysj.books.exception.script;

import com.pyk.bysj.books.exception.BaseException;

public class ScriptExecutionException extends BaseException {
  public ScriptExecutionException(String message, Integer code) {
    super(code, message);
  }
}
