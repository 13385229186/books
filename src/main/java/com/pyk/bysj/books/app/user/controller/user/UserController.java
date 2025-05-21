package com.pyk.bysj.books.app.user.controller.user;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.pyk.bysj.books.annotation.CurrentUser;
import com.pyk.bysj.books.app.user.service.admin.AdminUserService;
import com.pyk.bysj.books.config.UploadTmpConfig;
import com.pyk.bysj.books.exception.book.BookUploadException;
import com.pyk.bysj.books.exception.user.AvatarUploadException;
import com.pyk.bysj.books.mapper.UserMapper;
import com.pyk.bysj.books.model.dto.EbookDTO;
import com.pyk.bysj.books.model.dto.LoginDTO;
import com.pyk.bysj.books.app.user.service.user.UserService;
import com.pyk.bysj.books.model.dto.UserDTO;
import com.pyk.bysj.books.model.dto.UserUpdateDTO;
import com.pyk.bysj.books.model.entity.User;
import com.pyk.bysj.books.utils.ParseUtil;
import com.pyk.bysj.books.utils.PythonScriptExecutor;
import com.pyk.bysj.books.utils.ResponseData;
import com.pyk.bysj.books.utils.TokenBlacklist;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.pyk.bysj.books.utils.JwtTokenUtil.TOKEN_PREFIX;

@Validated
@RestController
@RequestMapping("/api/user")
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private AdminUserService adminUserService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private TokenBlacklist tokenBlacklist;

  @Autowired
  private UploadTmpConfig uploadTmpConfig;

  /**
   * 登录
   * @param loginDTO 登录信息DTO
   * @return ResponseData
   */
  @PostMapping("/login")
  public ResponseData login(@RequestBody @Valid LoginDTO loginDTO) {
    return userService.login(loginDTO);
  }

  /**
   * 注册
   * @param loginDTO 登录信息DTO
   * @return ResponseData
   */
  @PostMapping("/register")
  public ResponseData register(@RequestBody @Valid LoginDTO loginDTO) {
    // 加密密码
    String encodedPassword = passwordEncoder.encode(loginDTO.getPassword());
    return userService.register(loginDTO.getUsername(), encodedPassword);
  }

  /**
   * 完善或修改用户信息
   * @param updateDTO 修改项
   * @return ResponseData
   */
  @PostMapping("/updateInfo")
  public ResponseData updateInfo(
          @CurrentUser User user,
          @RequestHeader("Authorization") String authHeader,
          @RequestPart(value = "updateData", required = false) @Valid UserUpdateDTO updateDTO,
          @RequestPart(value = "avatarData", required = false) MultipartFile avatar
  ){
    System.out.println("updateDTO" + updateDTO);

    if(avatar != null){
      // 校验封面图片格式
      if (!Objects.requireNonNull(avatar.getContentType()).startsWith("image/")) {
        throw new AvatarUploadException("头像仅支持图片文件", 400);
      }

      // 临时存储
      String fileName = UUID.randomUUID()+ ".jpg";
      Path projectDir = Path.of(uploadTmpConfig.getBaseDir());
      Path avatarTempPath = projectDir.resolve(uploadTmpConfig.getAvatarUploads()).resolve(fileName);

      try {
        avatar.transferTo(avatarTempPath);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      Map<String, String> args = new HashMap<>();
      args.put("filePath", avatarTempPath.toString());
      args.put("fileName", fileName);

      // 调用Python解析
      String result = PythonScriptExecutor.executePythonScript("avatar_upload.py", args, 10);
      JSONObject jsonObject = JSONObject.parseObject(result);
      if(updateDTO == null){
        updateDTO = new UserUpdateDTO();
      }
      updateDTO.setAvatar(jsonObject.getString("avatar_path"));
    }


    String token = authHeader.substring(TOKEN_PREFIX.length());
    return userService.updateInfo(user, updateDTO, token);
  }

  @PostMapping("/getCurrentUser")
  public ResponseData getCurrentUser(@CurrentUser User user) {
    UserDTO userById = adminUserService.getUserById(user.getId());
    return ResponseData.success(userById);
  }

  @PostMapping("/outLogin")
  public ResponseData outLogin(@RequestHeader("Authorization") String authHeader) {
    String token = authHeader.substring(TOKEN_PREFIX.length());
    // 使用户当前token失效
    tokenBlacklist.addToBlacklist(token);
    return ResponseData.success();
  }





}
