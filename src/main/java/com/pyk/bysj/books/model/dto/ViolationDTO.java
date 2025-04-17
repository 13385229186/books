package com.pyk.bysj.books.model.dto;

import com.pyk.bysj.books.enums.ViolationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ViolationDTO {
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
  private ViolationType violationType;
  private LocalDateTime createdAt;

}
