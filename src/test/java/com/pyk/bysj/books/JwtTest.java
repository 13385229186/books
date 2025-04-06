//package com.pyk.bysj.books;
//
//import org.junit.jupiter.api.Test;
//
//import java.util.Calendar;
//import java.util.HashMap;
//
//import static org.springframework.security.config.Elements.JWT;
//
//
//public class JwtTest {
//  // 生成jwt
//  @Test
//  public void contextLoads() {
//    HashMap headers = new HashMap();
//    Calendar instance = Calendar.getInstance();
//    instance.add(Calendar.SECOND, 200);
//    String token = JWT.create().withHeader(headers) //headers
//            .withClaim("userid", "21")
//            .withClaim("username", "wangm") //payload
//            .withExpiresAt(instance.getTime())
//            .sign(Algorithm.HMAC256("1231312sdasddsas"));// 密钥
//    System.out.println(token);
//
//  }
//  // 验证jwt 可以获取页眉数据和有效荷载数据
//  @Test
//  public void checkToken() {
//    JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("1231312sdasddsas")).build();
//    DecodedJWT decodedJWT = jwtVerifier.verify("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NzI0OTMxMTAsInVzZXJpZCI6IjIxIiwidXNlcm5hbWUiOiJ3YW5nbSJ9.DNmrAFgRnWdcAtBK6nkwNPV-GSsrKyO_TyoIeB0YqzI");
//    String userid = decodedJWT.getClaim("userid").asString();
//    System.out.println(userid);
//
//  }
//}