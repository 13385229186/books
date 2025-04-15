package com.pyk.bysj.books.app.user.controller.user;

import com.pyk.bysj.books.annotation.CurrentUser;
import com.pyk.bysj.books.mapper.UserMapper;
import com.pyk.bysj.books.model.dto.LoginDTO;
import com.pyk.bysj.books.app.user.service.user.UserService;
import com.pyk.bysj.books.model.entity.User;
import com.pyk.bysj.books.utils.ResponseData;
import jakarta.validation.Valid;
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
   * @param loginDTO 登录信息DTO
   * @return ResponseData
   */
  @PostMapping("/login")
  public ResponseData login(@RequestBody @Valid LoginDTO loginDTO) {
    return userService.login(loginDTO);
  }

  /**
   * 注册
   * @param loginDTO 登录信息DTO
   * @return ResponseData
   */
  @PostMapping("/register")
  public ResponseData register(@RequestBody @Valid LoginDTO loginDTO) {
    // 加密密码
    String encodedPassword = passwordEncoder.encode(loginDTO.getPassword());
    return userService.register(loginDTO.getUsername(), encodedPassword);
  }

  /**
   * 完善或修改用户信息
   * @param name 真实姓名
   * @param phone 手机号
   * @return ResponseData
   */
  @PostMapping("/updateInfo")
  public ResponseData updateInfo(
          @CurrentUser User user,
          @RequestParam("name")
          @NotBlank(message = "姓名不能为空")
          String name,
          @RequestParam("phone")
          @NotBlank(message = "手机号不能为空")
          @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
          String phone) {
    return userService.updateInfo(user, name, phone);
  }

  @PostMapping("/getCurrentUser")
  public ResponseData getCurrentUser(@CurrentUser User user) {
    return ResponseData.success(user);
  }





}
