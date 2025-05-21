package com.pyk.bysj.books.model.dto;

import com.pyk.bysj.books.enums.ViolationType;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ViolationWithPageParamDTO {
  // 用户信息
  private String name;
  @Pattern(regexp = "^(|1[3-9]\\d{9})$", message = "手机号格式不正确")
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
