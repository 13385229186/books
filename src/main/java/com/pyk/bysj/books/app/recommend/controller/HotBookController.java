package com.pyk.bysj.books.app.recommend.controller;

import com.pyk.bysj.books.app.recommend.service.HotBookService;
import com.pyk.bysj.books.model.dto.PageParam;
import com.pyk.bysj.books.utils.ResponseData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class HotBookController {
  private final HotBookService hotBookService;

  @PostMapping("/hotBookList")
  public ResponseData hotBookList(
          @RequestBody @Valid PageParam pageParam
          ) {
    return ResponseData.success(hotBookService.hotBookList(pageParam));
  }

}
