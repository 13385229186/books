package com.pyk.bysj.books.model.dto;

import com.pyk.bysj.books.enums.ViolationType;
import lombok.Data;

@Data
public class UserViolationWithPageParamDTO {
  // 书籍信息
  private String title; // 书名

  // 违规信息
  private String violationType;
  private String createdAt;

  // 分页信息
  private PageParam pageParam = new PageParam();

  public ViolationType getViolationType() {
    return ViolationType.fromValue(violationType);
  }

  /**
   * 转为 ViolationWithPageParamDTO，用于用户受限的条件筛选
   * @return ViolationWithPageParamDTO
   */
  public ViolationWithPageParamDTO toViolationWithPageParamDTO(){
    return new ViolationWithPageParamDTO(title, violationType, createdAt, pageParam);
  }
}
