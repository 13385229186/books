package com.pyk.bysj.books.model.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Login{
  private Integer id;
  private Integer userId;
  private String username;
  private String password;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Login(Integer userId, String username, String password) {
    this.userId = userId;
    this.username = username;
    this.password = password;
  }

}
