package com.pyk.bysj.books.app.borrow.controller.user;

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
          @NotNull(message = "id不能为空")
          @Positive(message = "id必须为正整数")
          Integer bookId,
          @RequestParam("borrowDays")
          @NotNull(message = "天数不能为空")
          @Positive(message = "天数必须为正整数")
          Integer borrowDays
  ){
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
