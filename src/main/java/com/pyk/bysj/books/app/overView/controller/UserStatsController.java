package com.pyk.bysj.books.app.overView.controller;

import com.pyk.bysj.books.app.overView.service.UserStatsService;
import com.pyk.bysj.books.utils.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class UserStatsController {
  private final UserStatsService userStatsService;

  @PostMapping("/getRecentRegistrations")
  public ResponseData getRecentRegistrations(){
    return ResponseData.success(userStatsService.getRecentRegistrations());
  }

  @PostMapping("/getCreditDistribution")
  public ResponseData getCreditDistribution(){
    return ResponseData.success(userStatsService.getCreditDistribution());
  }

}
