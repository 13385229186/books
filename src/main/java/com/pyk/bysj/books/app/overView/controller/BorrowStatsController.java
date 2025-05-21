package com.pyk.bysj.books.app.overView.controller;

import com.pyk.bysj.books.app.overView.service.BorrowStatsService;
import com.pyk.bysj.books.utils.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class BorrowStatsController {
  private final BorrowStatsService borrowStatsService;

  @PostMapping("/getBorrowCountByCategory")
  public ResponseData getBorrowCountByCategory() {
    return ResponseData.success(borrowStatsService.getBorrowCountByCategory());
  }
}
