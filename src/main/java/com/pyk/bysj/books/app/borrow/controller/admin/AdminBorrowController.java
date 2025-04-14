package com.pyk.bysj.books.app.borrow.controller.admin;

import com.pyk.bysj.books.app.borrow.service.admin.AdminBorrowService;
import com.pyk.bysj.books.enums.BorrowStatus;
import com.pyk.bysj.books.model.dto.BorrowStatusDTO;
import com.pyk.bysj.books.utils.ParseUtil;
import com.pyk.bysj.books.utils.ResponseData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/admin")
public class AdminBorrowController {
  private final AdminBorrowService adminBorrowService;

  public AdminBorrowController(final AdminBorrowService adminBorrowService) {
    this.adminBorrowService = adminBorrowService;
  }

  @PostMapping("/setBorrowStatus")
  public ResponseData setBorrowStatus(
          @RequestBody @Valid BorrowStatusDTO borrowStatusDTO
  ){
//    if(borrowStatusDTO.getStatus() == BorrowStatus.APPLIED){
//      return ResponseData.fail("不可修改为已申请");
//    }
    return adminBorrowService.setBorrowStatus(borrowStatusDTO.getId(), borrowStatusDTO.getStatus());
  }

  @PostMapping("/handleBorrow")
  public ResponseData handleBorrow(
          @RequestParam("borrowId")
          @NotNull(message = "id不能为空")
          @Positive(message = "id必须为正整数")
          String borrowId
  ){
    return adminBorrowService.handleBorrow(ParseUtil.StringIdParseLong(borrowId));
  }


}
