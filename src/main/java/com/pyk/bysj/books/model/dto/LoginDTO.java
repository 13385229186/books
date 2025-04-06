package com.pyk.bysj.books.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDTO {
  @NotBlank(message = "用户名不能为空")
//  @Size(min = 4, max = 20, message = "用户名长度4-20个字符")
  private String username;

  @NotBlank(message = "密码不能为空")
//  @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
//          message = "密码至少8位，包含字母和数字")
  private String password;

  private Boolean rememberMe;

  public LoginDTO(String username, String password) {
    this.username = username;
    this.password = password;
  }
}
