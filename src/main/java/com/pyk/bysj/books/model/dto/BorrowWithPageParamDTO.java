package com.pyk.bysj.books.model.dto;

import com.pyk.bysj.books.enums.BorrowStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BorrowWithPageParamDTO {
  // 用户信息
  private String name;
  private String phone;

  // 书籍信息
  private String title; // 书名

  // 借阅信息
  private Integer id;
  private Integer userId;
  private Integer bookId;
  private Integer borrowDays;
  private String borrowTime;
  private String dueTime;
  private String returnTime;
  private String status;
  private String createdAt;

  // 分页信息
  @NotNull(message = "分页信息不能为空")
  private PageParam pageParam = new PageParam();

  public BorrowStatus getStatus() {
    return BorrowStatus.fromValue(status);
  }

  public BorrowWithPageParamDTO(String title, Integer borrowDays, String borrowTime, String dueTime, String returnTime, String status, String createdAt, PageParam pageParam) {
    this.title = title;
    this.borrowDays = borrowDays;
    this.borrowTime = borrowTime;
    this.dueTime = dueTime;
    this.returnTime = returnTime;
    this.status = status;
    this.createdAt = createdAt;
    this.pageParam = pageParam;
  }

}
