package com.pyk.bysj.books.model.dto.overView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyCountDTO {
  private LocalDateTime date;      // 统计日期
  private Integer count;  // 当日新增书籍数量

  // 日期格式化
  public String getFormattedDate() {
    return new SimpleDateFormat("yyyy-MM-dd").format(date);
  }
}