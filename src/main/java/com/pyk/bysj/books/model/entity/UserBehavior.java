package com.pyk.bysj.books.model.entity;

import com.pyk.bysj.books.enums.ActionType;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBehavior {
  private Long id;
  private Integer userId;
  private String isbn;
  @Getter
  private ActionType actionType;
  private Float weight;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

}
