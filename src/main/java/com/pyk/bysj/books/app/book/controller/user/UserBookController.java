package com.pyk.bysj.books.app.book.controller.user;

import com.pyk.bysj.books.app.book.service.user.UserBookService;
import com.pyk.bysj.books.model.dto.BookDTO;
import com.pyk.bysj.books.model.dto.PageParam;
import com.pyk.bysj.books.model.entity.Book;
import com.pyk.bysj.books.utils.ParseUtil;
import com.pyk.bysj.books.utils.ResponseData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Validated
@RestController
@RequestMapping("/user")
public class UserBookController {
  private final UserBookService bookService;

  @Autowired
  public UserBookController(UserBookService bookService) {
    this.bookService = bookService;
  }

  @PostMapping("/bookList")
  public ResponseData bookList(
          @RequestPart("bookData") @Valid BookDTO bookDTO
  ){
    // 提取分页信息
    PageParam pageParam = bookDTO.getPageParam();
    if (pageParam == null || pageParam.getCurrent() < 1 || pageParam.getPageSize() < 1) {
      throw new IllegalArgumentException("分页参数不合法");
    }
    if (pageParam.getPageSize() > 100) {
      throw new IllegalArgumentException("单页数量不能超过100");
    }
    // 提取筛选条件
    Book book = bookDTO.toEntity();
    try {
      Map<String, Object> bookMap = ParseUtil.toUnderlineMap(book);

      return ResponseData.success(bookService.bookList(bookMap, pageParam));
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @PostMapping("/getBookById")
  public ResponseData getBookById(
          @RequestParam("bookId")
          @NotNull(message = "id不能为空")
          @Positive(message = "id必须为正整数")
          Integer bookId
  ) {
    return ResponseData.success(bookService.getBookById(bookId));
  }

  @PostMapping("/getBookNumberById")
  public ResponseData getBookNumberById(
          @RequestParam("bookId")
          @NotNull(message = "id不能为空")
          @Positive(message = "id必须为正整数")
          Integer bookId
  ){
    return ResponseData.success(bookService.getBookNumberById(bookId));
  }

}
