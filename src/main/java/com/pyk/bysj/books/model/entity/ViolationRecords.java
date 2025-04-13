package com.pyk.bysj.books.model.entity;

import com.pyk.bysj.books.enums.ViolationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViolationRecords {
  private Long id;
  private Integer userId;
  private Long borrowId;
  @Getter
  private ViolationType violationType;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
