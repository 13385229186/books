package com.pyk.bysj.books.model.dto;

import com.pyk.bysj.books.enums.BorrowStatus;
import lombok.Data;
import lombok.Getter;

@Data
public class UserBorrowWithPageParamDTO {
  private Integer id;
  // 书籍信息
  private String title; // 书名

  private Integer borrowDays;
  private String borrowTime;
  private String dueTime;
  private String returnTime;
  private String createdAt;
  @Getter
  private BorrowStatus status;

  // 分页信息
  private PageParam pageParam = new PageParam();

//  public BorrowStatus getStatus() {
//    return BorrowStatus.fromValue(status);
//  }

  /**
   * 转为BorrowWithPageParamDTO，用于用户受限的条件筛选
   * @return BorrowWithPageParamDTO
   */
  public BorrowWithPageParamDTO toBorrowWithPageParamDTO(){
    return new BorrowWithPageParamDTO(id, title, borrowDays, borrowTime, dueTime, returnTime, status, createdAt, pageParam);
  }
}
