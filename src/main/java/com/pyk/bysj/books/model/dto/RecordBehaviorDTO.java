package com.pyk.bysj.books.model.dto;

import com.pyk.bysj.books.enums.BehaviorType;
import com.pyk.bysj.books.model.entity.UserBehavior;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecordBehaviorDTO {
  private Integer userId;
  @NotNull
  private Integer bookId;
  @NotNull
  private BehaviorType behaviorType;

  public UserBehavior toEntity(){
    return new UserBehavior(userId, bookId, behaviorType);
  }
}
