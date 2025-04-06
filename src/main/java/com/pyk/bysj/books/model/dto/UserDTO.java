package com.pyk.bysj.books.model.dto;

import com.pyk.bysj.books.enums.Role;
import lombok.Getter;

public class UserDTO {
  private Integer id;
  @Getter
  private Role role;
  private String name;
  private String phone;

  /**
   * 当前页数
   */
  private Integer current;

  /**
   * 每页数据条数
   */
  private Integer pageSize;
}
