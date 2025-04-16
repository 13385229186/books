package com.pyk.bysj.books.model.dto;

import com.pyk.bysj.books.enums.Role;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class UserWithPageParamDTO {
  private Integer id;
  private String avatar;
  private String username;
  private String name;
  private String phone;
  private String role;
  private Integer creditScore;

  public Role getRole() {
    return Role.fromValue(role);
  }

  @Valid
  private PageParam pageParam = new PageParam();
}

