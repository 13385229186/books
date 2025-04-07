package com.pyk.bysj.books.exception.handler;

import com.pyk.bysj.books.exception.BookUploadException;
import com.pyk.bysj.books.utils.ResponseData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * 处理书籍上传异常
   * @param ex 书籍上传异常
   * @return ResponseData
   */
  @ExceptionHandler(BookUploadException.class)
  public ResponseData handleBookUploadException(BookUploadException ex) {
    return ResponseData.fail(ex.getStatus(), ex.getMessage());
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseData handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
    return ResponseData.fail(400, "文件大小超过限制！");
  }

  /**
   * 处理Exception异常
   * @param ex Exception异常
   * @return ResponseData.fail(500, "系统繁忙")
   */
  @ExceptionHandler(Exception.class)
  public ResponseData handleGenericException(Exception ex) {
    return ResponseData.fail(500, "系统繁忙！");
  }
}
