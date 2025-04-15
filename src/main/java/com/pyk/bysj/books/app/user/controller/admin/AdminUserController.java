package com.pyk.bysj.books.app.user.controller.admin;

import com.pyk.bysj.books.app.user.service.admin.AdminUserService;
import com.pyk.bysj.books.app.user.service.user.UserService;
import com.pyk.bysj.books.model.dto.PageParam;
import com.pyk.bysj.books.model.dto.UserDTO;
import com.pyk.bysj.books.model.dto.UserWithPageParamDTO;
import com.pyk.bysj.books.model.entity.Book;
import com.pyk.bysj.books.utils.ParseUtil;
import com.pyk.bysj.books.utils.ResponseData;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Validated
@RestController
@RequestMapping("/admin")
public class AdminUserController {
  private final AdminUserService adminUserService;

  public AdminUserController(AdminUserService adminUserService) {
    this.adminUserService = adminUserService;
  }

  @PostMapping("/userList")
  public ResponseData userList(
          @RequestPart("userData") @Valid UserWithPageParamDTO userWithPageParamDTO
          ){
    // 提取分页信息
    PageParam pageParam = userWithPageParamDTO.getPageParam();
    // 提取筛选条件
    try {
      Map<String, Object> userMap = ParseUtil.toUnderlineMap(userWithPageParamDTO);
      userMap.remove("page_param");
      System.out.println(userMap);

      return ResponseData.success(adminUserService.userList(userMap, pageParam));
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
