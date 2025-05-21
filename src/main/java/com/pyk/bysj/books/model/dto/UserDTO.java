package com.pyk.bysj.books.model.dto;

import com.pyk.bysj.books.enums.Role;
import com.pyk.bysj.books.enums.UserStatus;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
public class UserDTO {
  private Integer id;
  private String avatar;
  private String username;
  private String name;
  private String phone;
  @Getter
  private UserStatus status;
  private Role role;
  private Integer creditScore;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
