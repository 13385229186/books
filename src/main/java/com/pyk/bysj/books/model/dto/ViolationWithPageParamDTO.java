package com.pyk.bysj.books.model.dto;

import com.pyk.bysj.books.enums.ViolationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ViolationWithPageParamDTO {
  // 用户信息
  private String name;
  private String phone;

  // 书籍信息
  private String title; // 书名

  // 违规信息
  private Long id;
  private Integer userId;
  private Integer bookId;
  private Long borrowId;
  private String violationType;
  private String createdAt;

  // 分页信息
  private PageParam pageParam = new PageParam();

  public ViolationType getViolationType() {
    return ViolationType.fromValue(violationType);
  }

  public ViolationWithPageParamDTO(String title, String violationType, String createdAt, PageParam pageParam){
    this.title = title;
    this.violationType = violationType;
    this.createdAt = createdAt;
    this.pageParam = pageParam;
  }
}
