package com.pyk.bysj.books.app.overView.controller;

import com.pyk.bysj.books.app.overView.service.BookStatsService;
import com.pyk.bysj.books.utils.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class BookStatsController {
  private final BookStatsService statsService;

  @PostMapping("/getBookOverview")
  public ResponseData getOverview() {
    return ResponseData.success(statsService.getBookOverview());
  }
}
