package com.pyk.bysj.books.model.dto.overView;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookOverviewVO {
  private Integer total;
  private Map<String, Integer> categories;
  private Map<String, Integer> formats;
  private List<DailyCountDTO> dailyNew;
}