package com.pyk.bysj.books.model.dto;

import com.pyk.bysj.books.enums.BorrowStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BorrowStatusDTO {
  @NotNull(message = "ID不能为空")
  @Positive(message = "ID必须是正整数")
  private Long id;
  @NotBlank(message = "status不能为空")
  private String status;

  public BorrowStatus getStatus() {
    return BorrowStatus.fromValue(status);
  }

}
