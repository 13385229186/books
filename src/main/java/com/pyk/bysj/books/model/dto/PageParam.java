package com.pyk.bysj.books.model.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@AllArgsConstructor
@NoArgsConstructor
// 通用分页参数
public class PageParam {
  /**
   * 当前页数
   */
  @Min(1)
  private Integer current = 1;

  /**
   * 每页数据条数
   */
  @Range(min = 1, max = 100)
  private Integer pageSize = 10;
}
