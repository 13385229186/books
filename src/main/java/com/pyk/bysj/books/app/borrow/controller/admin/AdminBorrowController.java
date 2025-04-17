package com.pyk.bysj.books.app.borrow.controller.admin;

import com.pyk.bysj.books.app.borrow.service.admin.AdminBorrowService;
import com.pyk.bysj.books.model.dto.BorrowWithPageParamDTO;
import com.pyk.bysj.books.model.dto.BorrowStatusDTO;
import com.pyk.bysj.books.model.dto.PageParam;
import com.pyk.bysj.books.model.dto.ViolationWithPageParamDTO;
import com.pyk.bysj.books.utils.ParseUtil;
import com.pyk.bysj.books.utils.ResponseData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
          @NotBlank(message = "id不能为空")
          String borrowId
  ){
    return adminBorrowService.handleBorrow(ParseUtil.StringIdParseLong(borrowId));
  }

  @PostMapping("/borrowList")
  public ResponseData borrowList(
          @RequestPart("borrowData") @Valid BorrowWithPageParamDTO borrowWithPageParamDTO
  ){
    // 提取分页信息
    PageParam pageParam = borrowWithPageParamDTO.getPageParam();
    // 提取筛选条件
    try {
      Map<String, Object> borrowMap = ParseUtil.toMap(borrowWithPageParamDTO);
      borrowMap.remove("pageParam");
      System.out.println(borrowMap);

      return ResponseData.success(adminBorrowService.borrowList(borrowMap, pageParam));
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @PostMapping("/violationList")
  public ResponseData violationList(
          @RequestPart("violationData") @Valid ViolationWithPageParamDTO violationWithPageParamDTO
  ){
    // 提取分页信息
    PageParam pageParam = violationWithPageParamDTO.getPageParam();
    // 提取筛选条件
    try {
      Map<String, Object> violationMap = ParseUtil.toMap(violationWithPageParamDTO);
      violationMap.remove("pageParam");
      System.out.println(violationMap);

      return ResponseData.success(adminBorrowService.violationList(violationMap, pageParam));
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }


}
