package com.pyk.bysj.books.app.user.controller.admin;

import com.pyk.bysj.books.app.user.service.admin.AdminUserService;
import com.pyk.bysj.books.app.user.service.user.UserService;
import com.pyk.bysj.books.enums.Role;
import com.pyk.bysj.books.enums.UserStatus;
import com.pyk.bysj.books.model.dto.LoginDTO;
import com.pyk.bysj.books.model.dto.PageParam;
import com.pyk.bysj.books.model.dto.UserDTO;
import com.pyk.bysj.books.model.dto.UserWithPageParamDTO;
import com.pyk.bysj.books.model.entity.Book;
import com.pyk.bysj.books.utils.ParseUtil;
import com.pyk.bysj.books.utils.ResponseData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

  @PostMapping("/changeRole")
  public ResponseData changeRole(
          @RequestParam("userId")
          @NotBlank(message = "id不能为空")
          String id,
          @RequestParam("role")
          @NotBlank(message = "角色不能为空")
          String role
  ){
    Integer userId = ParseUtil.StringIdParseInteger(id);
    return adminUserService.changeRole(userId, Role.fromValue(role));
  }

  @PostMapping("/changeStatus")
  public ResponseData changeStatus(
          @RequestParam("userId")
          @NotBlank(message = "id不能为空")
          String id,
          @RequestParam("status")
          @NotBlank(message = "用户状态不能为空")
          String status
  ){
    Integer userId = ParseUtil.StringIdParseInteger(id);
    return adminUserService.changeStatus(userId, UserStatus.fromValue(status));
  }

//  public ResponseData addUser(
//          @RequestPart("loginData") @Valid LoginDTO loginDTO
//  )

  @PostMapping("/getUserById")
  public ResponseData getUserById(
          @RequestParam("userId")
          @NotBlank(message = "id不能为空")
          String id
  ){
    Integer userId = ParseUtil.StringIdParseInteger(id);
    return ResponseData.success(adminUserService.getUserById(userId));
  }






}
