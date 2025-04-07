package com.pyk.bysj.books.exception.handler;

import com.pyk.bysj.books.utils.ResponseData;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ValidationExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseData handleValidation(MethodArgumentNotValidException ex) {
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
}
