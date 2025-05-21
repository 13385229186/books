package com.pyk.bysj.books.app.category.controller.user;

import com.pyk.bysj.books.app.category.service.user.UserCategoryService;
import com.pyk.bysj.books.utils.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/user")
public class UserCategoryController {
  private final UserCategoryService userCategoryService;

  public UserCategoryController(UserCategoryService userCategoryService) {
    this.userCategoryService = userCategoryService;
  }

  @PostMapping("/categoryList")
  public ResponseData categoryList(){
    return ResponseData.success(userCategoryService.categoriList());
  }

  // 增删改

}
