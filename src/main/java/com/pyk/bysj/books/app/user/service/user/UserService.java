package com.pyk.bysj.books.app.user.service.user;

import com.pyk.bysj.books.model.dto.LoginDTO;
import com.pyk.bysj.books.model.dto.UserDTO;
import com.pyk.bysj.books.model.dto.UserUpdateDTO;
import com.pyk.bysj.books.model.entity.User;
import com.pyk.bysj.books.utils.ResponseData;

import java.util.Map;

public interface UserService {
  /**
   * 登录
   * @param loginDTO loginDTO对象
   * @return ResponseData
   */
  ResponseData login(LoginDTO loginDTO);

  /**
   * 注册
   *
   * @param username 账号
   * @param password 密码
   * @return ResponseData
   */
  ResponseData register(String username, String password);

  /**
   * 完善或修改用户信息
   * @param user 当前用户
   * @param userUpdateDTO 修改项
   * @param token 用户jwt
   * @return ResponseData
   */
  ResponseData updateInfo(User user, UserUpdateDTO userUpdateDTO, String token);

  /**
   * 修改密码
   * @param oldPassword 旧密码，用户验证本人
   * @param newPassword 新密码
   * @return ResponseData
   */
  ResponseData updatePassword(String oldPassword, String newPassword);

  /**
   * 检验电话号码是否会重复
   * @param phone 电话号码
   * @param excludeUserId 排除user
   */
  void validatePhoneUniqueness(String phone, Integer excludeUserId);
}
