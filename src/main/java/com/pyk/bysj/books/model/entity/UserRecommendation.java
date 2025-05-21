package com.pyk.bysj.books.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户推荐结果实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRecommendation {
  /**
   * 用户推荐ID
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
   * 推荐分数
   */
  private Float score;

  /**
   * 创建时间
   */
  private LocalDateTime createdAt;

  /**
   * 更新时间
   */
  private LocalDateTime updatedAt;

  public UserRecommendation(Integer userId, Integer bookId, Float score) {
    this.userId = userId;
    this.bookId = bookId;
    this.score = score;
  }
}