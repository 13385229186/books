package com.pyk.bysj.books.model.entity;

import com.pyk.bysj.books.enums.BehaviorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户行为记录实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBehavior {
  /**
   * 行为ID
   */
  private Long id;

  /**
   * 用户ID
   */
  private Integer userId;

  /**
   * 书籍ID
   */
  private Integer bookId;

  /**
   * 行为类型
   */
  private BehaviorType behaviorType;

  /**
   * 行为权重
   */
  private Float weight;

  /**
   * 创建时间
   */
  private LocalDateTime createdAt;

  /**
   * 更新时间
   */
  private LocalDateTime updatedAt;

  public UserBehavior(Integer userId, Integer bookId, BehaviorType behaviorType) {
    this.userId = userId;
    this.bookId = bookId;
    this.behaviorType = behaviorType;
    this.weight = behaviorType.getWeight();
  }


}