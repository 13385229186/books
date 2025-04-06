package com.pyk.bysj.books.utils;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.csrf.CsrfException;

public class AuthExceptionUtil {
  public static ResponseData getErrMsgByExceptionType(AuthenticationException e) {
    if (e instanceof LockedException) {
      return ResponseData.fail(1100, "账户被锁定，请联系管理员!");
    } else if (e instanceof CredentialsExpiredException) {
      return ResponseData.fail(1105,"用户名或者密码输入错误!");
    }else if (e instanceof InsufficientAuthenticationException) {
      return ResponseData.fail(403,"权限不足请重新登录!");
    } else if (e instanceof AccountExpiredException) {
      return ResponseData.fail(1101, "账户过期，请联系管理员!");
    } else if (e instanceof DisabledException) {
      return ResponseData.fail(1102, ("账户被禁用，请联系管理员!"));
    } else if (e instanceof BadCredentialsException) {
      return ResponseData.fail(1105, "用户名或者密码输入错误!");
    }else if (e instanceof AuthenticationServiceException) {
      return ResponseData.fail(1106, "认证失败，请重试!");
    }

    return ResponseData.fail(1200, e.getMessage());
  }
  public static ResponseData getErrMsgByExceptionType(AccessDeniedException e) {
    if (e instanceof CsrfException) {
      return ResponseData.fail(-1001, "非法访问跨域请求异常!");
    }else if (e instanceof AuthorizationServiceException) {
      return ResponseData.fail(1101, "认证服务异常请重试!");
    }else if (e instanceof AccessDeniedException) {
      return ResponseData.fail(4003, "权限不足不允许访问!");
    }

    return ResponseData.fail(1200, e.getMessage());
  }
}