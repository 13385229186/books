package com.pyk.bysj.books.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EbookDTO {
  String bookName;
  String original_path;
  String cover_path;
}
