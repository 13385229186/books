package com.pyk.bysj.books.filter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pyk.bysj.books.enums.UserStatus;
import com.pyk.bysj.books.exception.security.JwtAuthenticationException;
import com.pyk.bysj.books.exception.user.UserStatusException;
import com.pyk.bysj.books.mapper.LoginMapper;
import com.pyk.bysj.books.mapper.UserMapper;
import com.pyk.bysj.books.model.LoginUserDetails;
import com.pyk.bysj.books.model.entity.Login;
import com.pyk.bysj.books.model.entity.User;
import com.pyk.bysj.books.utils.JwtTokenUtil;
import com.pyk.bysj.books.utils.ResponseData;
import com.pyk.bysj.books.utils.TokenBlacklist;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.IOException;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.*;

/**
 * token过滤器 验证token有效性
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
  private final LoginMapper loginMapper;
  private final UserMapper userMapper;
  private final JwtTokenUtil jwtTokenUtil;
  private final TokenBlacklist tokenBlacklist;

  @Autowired
  public JwtAuthenticationTokenFilter(LoginMapper loginMapper, UserMapper userMapper, JwtTokenUtil jwtTokenUtil, TokenBlacklist tokenBlacklist) {
    this.loginMapper = loginMapper;
    this.userMapper = userMapper;
    this.jwtTokenUtil = jwtTokenUtil;
    this.tokenBlacklist = tokenBlacklist;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException, java.io.IOException {
    System.out.println("77777777777777777");

    String token = request.getHeader(JwtTokenUtil.TOKEN_HEADER);
    if (StringUtils.isBlank(token) || !token.startsWith(JwtTokenUtil.TOKEN_PREFIX)){
      chain.doFilter(request,response);
      return;
    }
    try {
      //如果能获取到token则Authentication进行设置，表示已认证
      SecurityContextHolder.getContext().setAuthentication(getAuthentication(token));
    } catch (Exception e) {
      System.out.println("认证失败！！！");
      tokenBlacklist.addToBlacklist(token);
      // 终止后续处理
    }
    System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaa");
    //继续执行其他过滤器的逻辑
    chain.doFilter(request,response);
  }

  private UsernamePasswordAuthenticationToken getAuthentication(String tokenHeader) throws Exception {
    System.out.println("5555555555555555555555555");

    String token = tokenHeader.replace(JwtTokenUtil.TOKEN_PREFIX,"");

    System.out.println("token: " + token);
    //判断token是否过期
    boolean expiration = jwtTokenUtil.isTokenExpired(token);
    if (expiration || tokenBlacklist.isBlacklisted(token)){
      System.out.println("*********token已过期，请重新登录*******");
      throw new JwtException("token已过期，请重新登录");
    }else{
      String username = jwtTokenUtil.getUserNameFromToken(token);

      System.out.println("username = " + username);
      
      Login login = loginMapper.selectOne(
              new LambdaQueryWrapper<Login>().eq(Login::getUsername, username)
      );
      if (login == null) {
        throw new UsernameNotFoundException("用户不存在");
      }
      User user = userMapper.selectById(login.getUserId());
      System.out.println("=========================");
      System.out.println(login);
      System.out.println(user.getStatus());
      System.out.println("=========================");
      if(user.getStatus() != UserStatus.ACTIVE){
        tokenBlacklist.addToBlacklist(token);
        throw new UserStatusException("用户状态异常，请联系管理员");
      }
      LoginUserDetails loginUserDetails = new LoginUserDetails(user, login, "ROLE_" + user.getRole());

      System.out.println("loginUserDetails = " + loginUserDetails);
      
      //新建一个UsernamePasswordAuthenticationToken用来设置Authentication
      UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUserDetails, null, loginUserDetails.getAuthorities());
      return authenticationToken;
    }
  }
}

