//package com.pyk.bysj.books.filter;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.pyk.bysj.books.model.dto.LoginDTO;
//import com.pyk.bysj.books.model.entity.Login;
//import com.pyk.bysj.books.model.entity.User;
//import com.pyk.bysj.books.utils.JwtTokenUtil;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//
//  private ThreadLocal<Boolean> rememberMe = new ThreadLocal<>();
//  private AuthenticationManager authenticationManager;
//
//  @Autowired
//  private JwtTokenUtil jwtTokenUtil;
//
//  public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
//    this.authenticationManager = authenticationManager;
//    super.setFilterProcessesUrl("/auth/login");
//  }
//
//  @Override
//  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//
//    // 从输入流中获取到登录的信息
//    try {
//      LoginDTO dto = new ObjectMapper().readValue(request.getInputStream(), LoginDTO.class);
//      rememberMe.set(dto.getRememberMe() != null && dto.getRememberMe());
//      return authenticationManager.authenticate(
//              new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword(), new ArrayList<>())
//      );
//    } catch (IOException e) {
//      e.printStackTrace();
//      return null;
//    }
//  }
//
//  // 成功验证后调用的方法
//  // 如果验证成功，就生成token并返回
//  @Override
//  protected void successfulAuthentication(HttpServletRequest request,
//                                          HttpServletResponse response,
//                                          FilterChain chain,
//                                          Authentication authResult) throws IOException, ServletException {
//
//    Login login = (Login) authResult.getPrincipal();
//    System.out.println("loginUser:" + login.toString());
//    User.Role role = User.Role.USER;
//    String token = jwtTokenUtil.generateToken(login.getUsername(), role, rememberMe.get());
//        /* 返回创建成功的token
//         但是这里创建的token只是单纯的token
//         按照jwt的规定，最后请求的时候应该是 `Bearer token`*/
//    response.setHeader("token", JwtTokenUtil.TOKEN_PREFIX + token);
//  }
//
//  @Override
//  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
//    response.getWriter().write("authentication failed, reason: " + failed.getMessage());
//  }
//}
//
