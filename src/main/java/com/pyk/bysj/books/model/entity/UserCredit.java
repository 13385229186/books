package com.pyk.bysj.books.model.entity;

import com.pyk.bysj.books.enums.ActionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCredit {
  private Long id;
  private Integer userId;
  private Integer creditScore;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public UserCredit(Integer userId) {
    this.userId = userId;
  }
}
