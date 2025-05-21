package com.pyk.bysj.books.exception.handler;

import com.pyk.bysj.books.exception.book.BookUploadException;
import com.pyk.bysj.books.exception.borrow.BorrowStatusException;
import com.pyk.bysj.books.exception.general.NotExistException;
import com.pyk.bysj.books.exception.general.OperationFailedException;
import com.pyk.bysj.books.exception.general.SqlFailedException;
import com.pyk.bysj.books.exception.borrow.BookNotAvailableException;
import com.pyk.bysj.books.exception.borrow.CreditException;
import com.pyk.bysj.books.exception.script.ScriptExecutionException;
import com.pyk.bysj.books.exception.user.AvatarUploadException;
import com.pyk.bysj.books.exception.user.UserStatusException;
import com.pyk.bysj.books.utils.ResponseData;
import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * 处理认证失败异常
   */
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseData handleBadCredentialsException(BadCredentialsException ex) {
    return ResponseData.fail(401, ex.getMessage());
  }

  @ExceptionHandler(UserStatusException.class)
  public ResponseData handleUserStatusException(UserStatusException ex) {
    return ResponseData.fail(ex.getCode(), ex.getMessage());
  }

  // validation
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseData handleValidation(MethodArgumentNotValidException ex) {
    List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());

    return ResponseData.fail(400, errors.get(0), errors);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseData handleConstraintViolation(ConstraintViolationException ex) {
    List<String> errors = ex.getConstraintViolations().stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .collect(Collectors.toList());
    return ResponseData.fail(400, errors.get(0), errors);
  }

  // script
  @ExceptionHandler(ScriptExecutionException.class)
  public ResponseData handleScriptExecutionException(ScriptExecutionException ex) {
    return ResponseData.fail(ex.getCode(), ex.getMessage());
  }

  // user
  @ExceptionHandler(AvatarUploadException.class)
  public ResponseData handleAvatarUploadException(AvatarUploadException ex) {
    return ResponseData.fail(ex.getCode(), ex.getMessage());
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
    return ResponseData.fail(400, ex.getMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseData handleAccessDeniedException(AccessDeniedException ex) {
    return ResponseData.fail(400, ex.getMessage());
  }

  @ExceptionHandler(MissingServletRequestPartException.class)
  public ResponseData handleMissingServletRequestPartException(MissingServletRequestPartException ex) {
    return ResponseData.fail(400, ex.getMessage());
  }

  @ExceptionHandler(MultipartException.class)
  public ResponseData handleMultipartException(MultipartException ex) {
    return ResponseData.fail(400, ex.getMessage());
  }

  @ExceptionHandler(DateTimeParseException.class)
  public ResponseData handleDateTimeParseException(DateTimeParseException ex) {
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
