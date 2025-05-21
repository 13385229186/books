package com.pyk.bysj.books.app.recommend.controller;

import com.pyk.bysj.books.annotation.CurrentUser;
import com.pyk.bysj.books.app.recommend.service.UserBehaviorService;
import com.pyk.bysj.books.model.dto.RecordBehaviorDTO;
import com.pyk.bysj.books.model.entity.UserBehavior;
import com.pyk.bysj.books.utils.ResponseData;
import com.pyk.bysj.books.model.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserBehaviorController {
  private final UserBehaviorService userBehaviorService;

  @PostMapping("/recordBehavior")
  public ResponseData recordBehavior(
          @CurrentUser User user,
          @RequestBody @Valid RecordBehaviorDTO recordBehaviorDTO
          ){
    UserBehavior userBehavior = recordBehaviorDTO.toEntity();
    userBehavior.setUserId(user.getId());
    return ResponseData.success(userBehaviorService.recordBehavior(userBehavior));
  }

}
