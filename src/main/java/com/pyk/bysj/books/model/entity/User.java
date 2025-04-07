package com.pyk.bysj.books.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pyk.bysj.books.enums.Role;
import com.pyk.bysj.books.enums.UserStatus;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User{
  private Integer id;
  private String avatar;
  private String name;
  private String phone;
  @Getter
  private Role role;
  @Getter
  private UserStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public User(Role role) {
    this.role = role;
  }

}
