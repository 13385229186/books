package com.pyk.bysj.books.app.user.controller.user;

import com.pyk.bysj.books.mapper.UserMapper;
import com.pyk.bysj.books.model.dto.LoginDTO;
import com.pyk.bysj.books.app.user.service.user.UserService;
import com.pyk.bysj.books.utils.ResponseData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/user")
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private UserMapper userMapper;

  /**
   * 登录
   * @param username 用户名
   * @param password 密码
   * @return ResponseData
   */
  @PostMapping("/login")
  public ResponseData login(@RequestParam("username")String username, @RequestParam("password")String password) {
    LoginDTO loginDTO = new LoginDTO(username, password);
    return userService.login(loginDTO);
  }

  /**
   * 注册
   * @param username 用户名
   * @param password 密码
   * @return ResponseData
   */
  @PostMapping("/register")
  public ResponseData register(@RequestParam("username")String username, @RequestParam("password")String password) {
    if(username == null || password == null) {
      return ResponseData.fail("账号或密码不能为空！");
    }
    // 加密密码
    String encodedPassword = passwordEncoder.encode(password);
    return userService.register(username, encodedPassword);
  }

  /**
   * 完善或修改用户信息
   * @param name 真实姓名
   * @param phone 手机号
   * @return ResponseData
   */
  @PostMapping("/updateInfo")
  public ResponseData updateInfo(@RequestParam("name") @NotBlank(message = "姓名不能为空") String name,
                           @RequestParam("phone") @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String phone) {
    return userService.updateInfo(name, phone);
  }





}
