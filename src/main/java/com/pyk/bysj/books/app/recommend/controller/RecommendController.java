package com.pyk.bysj.books.app.recommend.controller;

import com.pyk.bysj.books.annotation.CurrentUser;
import com.pyk.bysj.books.app.recommend.service.RecommendService;
import com.pyk.bysj.books.model.dto.PageParam;
import com.pyk.bysj.books.model.entity.User;
import com.pyk.bysj.books.utils.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class RecommendController {
  private final RecommendService recommendService;

  @PostMapping("/recommendations")
  public ResponseData getRecommendations(
          @CurrentUser User user,
          @RequestBody PageParam pageParam) {
    return ResponseData.success(recommendService.getRecommendations(user.getId(), pageParam));
  }
}
