package com.pyk.bysj.books.exception.auth;

import com.alibaba.fastjson2.JSON;
import com.pyk.bysj.books.utils.AuthExceptionUtil;
import com.pyk.bysj.books.utils.WebUtil;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
  @Override
  public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
    WebUtil.renderJson(httpServletResponse, JSON.toJSONString(AuthExceptionUtil.getErrMsgByExceptionType(e)));
  }
}
