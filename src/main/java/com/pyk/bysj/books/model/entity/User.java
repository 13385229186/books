package com.pyk.bysj.books.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pyk.bysj.books.enums.Role;
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
  @Getter
  private Role role;
  private String name;
  private String phone;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public User(Role role) {
    this.role = role;
  }

}
