package com.pyk.bysj.books.exception.handler.auth;

import com.alibaba.fastjson2.JSON;
import com.pyk.bysj.books.utils.AuthExceptionUtil;
import com.pyk.bysj.books.utils.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
  @Override
  public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
    WebUtil.renderJson(httpServletResponse, JSON.toJSONString(AuthExceptionUtil.getErrMsgByExceptionType(e)));
  }
}
