package com.pyk.bysj.books.app.book.controller.user;

import com.pyk.bysj.books.app.book.service.user.UserBookService;
import com.pyk.bysj.books.model.dto.BookDTO;
import com.pyk.bysj.books.model.dto.PageParam;
import com.pyk.bysj.books.model.entity.Book;
import com.pyk.bysj.books.utils.ObjectToMapUtil;
import com.pyk.bysj.books.utils.ResponseData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
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
      Map<String, Object> bookMap = ObjectToMapUtil.toUnderlineMap(book);

      return ResponseData.success(bookService.bookList(bookMap, pageParam));
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @PostMapping("/getBookById")
  public ResponseData getBookById(
          @RequestParam("id") @Positive Integer id
  ) {
    return ResponseData.success(bookService.getBookById(id));
  }

  @PostMapping("/getBookNumberById")
  public ResponseData getBookNumberById(
          @RequestParam("id") @Positive Integer id
  ){
    return ResponseData.success(bookService.getBookNumberById(id));
  }



}
