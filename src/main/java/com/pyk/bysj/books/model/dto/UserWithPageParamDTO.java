package com.pyk.bysj.books.model.dto;

import com.pyk.bysj.books.enums.Role;
import com.pyk.bysj.books.enums.UserStatus;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.Getter;

@Data
public class UserWithPageParamDTO {
  private Integer id;
  private String avatar;
  private String username;
  private String name;
  private String phone;
  private UserStatus status;
  @Getter
  private Role role;
  private Integer creditScore;

//  public Role getRole() {
//    return Role.fromValue(role);
//  }

  @Valid
  private PageParam pageParam = new PageParam();
}

