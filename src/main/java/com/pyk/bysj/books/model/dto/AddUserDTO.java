package com.pyk.bysj.books.model.dto;

import com.pyk.bysj.books.enums.Role;
import com.pyk.bysj.books.enums.UserStatus;
import com.pyk.bysj.books.model.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddUserDTO {
  @NotBlank(message = "用户名不能为空")
  private String username;

  @NotBlank(message = "密码不能为空")
  private String password;

  private String name;

  @Pattern(regexp = "^(|1[3-9]\\d{9})$", message = "手机号格式不正确")
  private String phone;

  private Role role = Role.USER;
  private UserStatus status = UserStatus.ACTIVE;

  /**
   * 用户管理员添加用户时使用
   * @return User
   */
  public User toUser(){
    User user = new User(role);
    user.setName(name);
    user.setPhone(phone);
    user.setStatus(status);
    return user;
  }
}
