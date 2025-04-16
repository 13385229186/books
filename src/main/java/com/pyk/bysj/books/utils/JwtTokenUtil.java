package com.pyk.bysj.books.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.pyk.bysj.books.enums.Role;
import com.pyk.bysj.books.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {

  private static final String CLAIM_KEY_USERNAME = "sub";
  private static final String CLAIM_KEY_CREATED = "created";
  private static final String CLAIM_KEY_ROLE = "role";
  private static final String CLAIM_KEY_REMEMBER_ME = "rememberMe";

  public static final String TOKEN_HEADER = "Authorization";
  public static final String TOKEN_PREFIX = "Bearer ";

  @Value("${jwt.secret-key}")
  private String secretKey;
  @Value("${jwt.expiration-time}")
  private Integer expirationTime;

  /**
   * 生成token
   */
  private String generateToken(Map<String, Object> claims) {
    return Jwts.builder()
            .setClaims(claims)
            .setExpiration(generateExpirationDate())
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
  }

  /**
   * 解析request中的token信息
   *
   * @param request
   * @return Claims
   */
  private Claims getClaimsFromToken(HttpServletRequest request) {
    String token = request.getHeader(TOKEN_PREFIX);
    return getClaimsFromToken(token);
  }

  /**
   * 从token中获取JWT中的负载
   */
  private Claims getClaimsFromToken(String token) {
    return Jwts.parser()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
  }

  /**
   * 生成token的过期时间
   */
  private Date generateExpirationDate() {
    return new Date(System.currentTimeMillis() + expirationTime * 1000);
  }

  /**
   * 从token中获取登录用户名
   */
  public String getUserNameFromToken(String token) {
    String username;
    try {
      Claims claims = getClaimsFromToken(token);
      username = claims.getSubject();
    } catch (Exception e) {
      username = null;
    }
    return username;
  }

  /**
   * 验证token是否还有效
   *
   * @param token       客户端传入的token
   * @param userAccount 从数据库中查询出来的用户信息
   */
  public boolean validateToken(String token, String userAccount) {
    String username = getUserNameFromToken(token);
    return username.equals(userAccount) && !isTokenExpired(token);
  }

  /**
   * 判断token是否已经失效
   */
  public boolean isTokenExpired(String token) {
    Date expiredDate = getExpiredDateFromToken(token);
    return expiredDate.before(new Date());
  }

  /**
   * 从token中获取过期时间
   */
  public Date getExpiredDateFromToken(String token) {
    Claims claims = getClaimsFromToken(token);
    return claims.getExpiration();
  }

  /**
   * 根据用户信息生成token
   *
   * @param userAccount
   */
  public String generateToken(String userAccount) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(CLAIM_KEY_USERNAME, userAccount);
    claims.put(CLAIM_KEY_CREATED, new Date());
    return generateToken(claims);
  }

  /**
   * 根据用户信息生成token
   *
   * @param userAccount
   * @param role
   * @param rememberMe
   * @return
   */
  public String generateToken(String userAccount, Role role, Boolean rememberMe) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(CLAIM_KEY_USERNAME, userAccount);
    claims.put(CLAIM_KEY_CREATED, new Date());
    claims.put(CLAIM_KEY_ROLE, role);
    claims.put(CLAIM_KEY_REMEMBER_ME, rememberMe);
    return generateToken(claims);
  }

  /**
   * 当原来的token没过期时是可以刷新的
   *
   * @param oldToken 带tokenHead的token
   */
  public String refreshHeadToken(String oldToken) {
    if (StrUtil.isEmpty(oldToken)) {
      return null;
    }
    String token = oldToken.substring(TOKEN_PREFIX.length());
    if (StrUtil.isEmpty(token)) {
      return null;
    }
    //token校验不通过
    Claims claims = getClaimsFromToken(token);
    if (claims == null) {
      return null;
    }
    //如果token已经过期，不支持刷新
    if (isTokenExpired(token)) {
      return null;
    }
    //如果token在30分钟之内刚刷新过，返回原token
    if (tokenRefreshJustBefore(token, 30 * 60)) {
      return token;
    } else {
      claims.put(CLAIM_KEY_CREATED, new Date());
      return generateToken(claims);
    }
  }

  /**
   * 判断token在指定时间内是否刚刚刷新过
   *
   * @param token 原token
   * @param time  指定时间（秒）
   */
  private boolean tokenRefreshJustBefore(String token, int time) {
    Claims claims = getClaimsFromToken(token);
    Date created = claims.get(CLAIM_KEY_CREATED, Date.class);
    Date refreshDate = new Date();
    //刷新时间在创建时间的指定时间内
    if (refreshDate.after(created) && refreshDate.before(DateUtil.offsetSecond(created, time))) {
      return true;
    }
    return false;
  }

}