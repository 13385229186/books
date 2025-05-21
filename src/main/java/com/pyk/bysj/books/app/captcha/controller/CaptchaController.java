package com.pyk.bysj.books.app.captcha.controller;

import com.pyk.bysj.books.utils.CaptchaUtil;
import com.pyk.bysj.books.utils.RedisUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class CaptchaController {
  private final RedisUtil redisUtil;

  public CaptchaController(RedisUtil redisUtil) {
    this.redisUtil = redisUtil;
  }

  @GetMapping("/captcha")
  public void getCaptcha(@RequestParam String token,
                         HttpServletResponse response) throws IOException {
    String code = CaptchaUtil.generateCode();
    System.out.println("code = " + code);
    redisUtil.set("captcha:" + token, code, 300);
    response.getOutputStream().write(CaptchaUtil.generateImage(code));
  }

  @GetMapping("/verify")
  public boolean verifyCode(@RequestParam String token,
                            @RequestParam String code) {
    String storedCode = redisUtil.get("captcha:" + token).toString();
    return code.equals(storedCode);
  }
}