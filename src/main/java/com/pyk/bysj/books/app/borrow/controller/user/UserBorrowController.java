package com.pyk.bysj.books.app.borrow.controller.user;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.pyk.bysj.books.annotation.CurrentUser;
import com.pyk.bysj.books.app.book.service.user.UserBookService;
import com.pyk.bysj.books.app.borrow.service.admin.AdminBorrowService;
import com.pyk.bysj.books.app.borrow.service.user.UserBorrowService;
import com.pyk.bysj.books.model.dto.*;
import com.pyk.bysj.books.model.entity.User;
import com.pyk.bysj.books.utils.ParseUtil;
import com.pyk.bysj.books.utils.ResponseData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/user")
public class UserBorrowController {
  private final UserBorrowService borrowService;
  private final AdminBorrowService adminBorrowService;

  @Autowired
  public UserBorrowController(UserBorrowService borrowService, AdminBorrowService adminBorrowService) {
    this.borrowService = borrowService;
    this.adminBorrowService = adminBorrowService;
  }

  @PostMapping("/borrowBook")
  public ResponseData borrowBook(
          @CurrentUser User user,
          @RequestParam("bookId")
          @NotBlank(message = "id不能为空")
          String id,
          @RequestParam("borrowDays")
          @NotBlank(message = "天数不能为空")
          String bd
  ){
    if(StringUtils.isBlank(user.getPhone())){
      return ResponseData.fail("请先完善手机号等信息");
    }

    Integer bookId = ParseUtil.StringIdParseInteger(id);
    Integer borrowDays = ParseUtil.StringIdParseInteger(bd, "天数");
    return borrowService.borrowBook(user, bookId, borrowDays);
  }

  @PostMapping("/cancelBorrowBook")
  public ResponseData cancelBorrowBook(
          @CurrentUser User user,
          @RequestParam("borrowId")
          @NotBlank(message = "id不能为空")
          String borrowId
  ){
    return borrowService.cancelBorrowBook(user, ParseUtil.StringIdParseLong(borrowId));
  }

  @PostMapping("/borrowListByUser")
  public ResponseData borrowListByUser(
          @CurrentUser User user,
          @RequestBody @Valid UserBorrowWithPageParamDTO userBorrowWithPageParamDTO
  ){
    System.out.println("userBorrowWithPageParamDTO" + userBorrowWithPageParamDTO);
    // 提取筛选条件，加入当前用户id
    BorrowWithPageParamDTO borrowWithPageParamDTO = userBorrowWithPageParamDTO.toBorrowWithPageParamDTO();
    borrowWithPageParamDTO.setUserId(user.getId());
    // 提取分页信息
    PageParam pageParam = borrowWithPageParamDTO.getPageParam();
    // 提取筛选条件
    try {
      Map<String, Object> borrowMap = ParseUtil.toMap(borrowWithPageParamDTO);
      borrowMap.remove("pageParam");
      return ResponseData.success(adminBorrowService.borrowList(borrowMap, pageParam));
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @PostMapping("/violationListByUser")
  public ResponseData violationListByUser(
          @CurrentUser User user,
          @RequestBody @Valid UserViolationWithPageParamDTO userViolationWithPageParamDTO
  ){
    // 提取筛选条件，加入当前用户id
    ViolationWithPageParamDTO violationWithPageParamDTO = userViolationWithPageParamDTO.toViolationWithPageParamDTO();
    violationWithPageParamDTO.setUserId(user.getId());
    // 提取分页信息
    PageParam pageParam = violationWithPageParamDTO.getPageParam();
    // 提取筛选条件
    try {
      Map<String, Object> violationMap = ParseUtil.toMap(violationWithPageParamDTO);
      violationMap.remove("pageParam");
      return ResponseData.success(adminBorrowService.violationList(violationMap, pageParam));
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

}
