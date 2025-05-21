package com.pyk.bysj.books.model.dto.overView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCountDTO {
  private String categoryName;
  private Integer count;
}