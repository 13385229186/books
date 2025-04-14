package com.pyk.bysj.books.exception.handler;

import com.pyk.bysj.books.exception.book.BookUploadException;
import com.pyk.bysj.books.exception.borrow.BorrowStatusException;
import com.pyk.bysj.books.exception.general.NotExistException;
import com.pyk.bysj.books.exception.general.OperationFailedException;
import com.pyk.bysj.books.exception.general.SqlFailedException;
import com.pyk.bysj.books.exception.borrow.BookNotAvailableException;
import com.pyk.bysj.books.exception.borrow.CreditException;
import com.pyk.bysj.books.utils.ResponseData;
import jakarta.validation.ConstraintViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // validation
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseData handleValidation(MethodArgumentNotValidException ex) {
    System.out.println("**************");
    List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());

    return ResponseData.fail(400, "参数校验失败", errors);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseData handleConstraintViolation(ConstraintViolationException ex) {
    List<String> errors = ex.getConstraintViolations().stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .collect(Collectors.toList());
    return ResponseData.fail(400, "参数校验失败", errors);
  }

  // book
  /**
   * 处理书籍上传异常
   * @param ex 书籍上传异常
   * @return ResponseData
   */
  @ExceptionHandler(BookUploadException.class)
  public ResponseData handleBookUploadException(BookUploadException ex) {
    return ResponseData.fail(ex.getCode(), ex.getMessage());
  }

  // borrow
  @ExceptionHandler(BookNotAvailableException.class)
  public ResponseData handleBookNotAvailableException(BookNotAvailableException ex) {
    return ResponseData.fail(ex.getCode(), ex.getMessage());
  }

  @ExceptionHandler(CreditException.class)
  public ResponseData handleCreditException(CreditException ex) {
    return ResponseData.fail(ex.getCode(), ex.getMessage());
  }

  @ExceptionHandler(BorrowStatusException.class)
  public ResponseData handleBorrowStatusException(BorrowStatusException ex) {
    return ResponseData.fail(ex.getCode(), ex.getMessage());
  }

  // framework
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseData handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
    return ResponseData.fail(400, "文件大小超过限制！");
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseData handleIllegalArgumentException(IllegalArgumentException ex) {
    System.out.println("******************");
    return ResponseData.fail(400, ex.getMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseData handleAccessDeniedException(AccessDeniedException ex) {
    return ResponseData.fail(400, ex.getMessage());
  }

  // general
  @ExceptionHandler(SqlFailedException.class)
  public ResponseData handleSqlFailedException(SqlFailedException ex) {
    return ResponseData.fail(ex.getCode(), ex.getMessage());
  }
  @ExceptionHandler(OperationFailedException.class)
  public ResponseData handleOperationFailedException(OperationFailedException ex) {
    return ResponseData.fail(ex.getCode(), ex.getMessage());
  }
  @ExceptionHandler(NotExistException.class)
  public ResponseData handleNotExistException(NotExistException ex) {
    return ResponseData.fail(ex.getCode(), ex.getMessage());
  }

  // 最终Exception

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
