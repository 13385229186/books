package com.pyk.bysj.books.model.dto;

import com.pyk.bysj.books.enums.Role;
import lombok.Data;
import lombok.Getter;

@Data
public class UserDTO {
  private Integer id;
  private String avatar;
  private String name;
  private String phone;
  @Getter
  private Role role;

  private PageParam pageParam = new PageParam();
}
