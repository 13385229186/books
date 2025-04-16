package com.pyk.bysj.books.app.borrow.controller.user;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.pyk.bysj.books.annotation.CurrentUser;
import com.pyk.bysj.books.app.book.service.user.UserBookService;
import com.pyk.bysj.books.app.borrow.service.user.UserBorrowService;
import com.pyk.bysj.books.model.entity.User;
import com.pyk.bysj.books.utils.ParseUtil;
import com.pyk.bysj.books.utils.ResponseData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/user")
public class UserBorrowController {
  private final UserBorrowService borrowService;

  @Autowired
  public UserBorrowController(UserBorrowService borrowService) {
    this.borrowService = borrowService;
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

}
