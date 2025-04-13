package com.pyk.bysj.books.app.user.service.user;

import com.pyk.bysj.books.model.dto.LoginDTO;
import com.pyk.bysj.books.model.entity.User;
import com.pyk.bysj.books.utils.ResponseData;

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
   * @param name 真实姓名
   * @param phone 手机号
   * @return ResponseData
   */
  ResponseData updateInfo(User user, String name, String phone);

  /**
   * 修改密码
   * @param oldPassword 旧密码，用户验证本人
   * @param newPassword 新密码
   * @return ResponseData
   */
  ResponseData updatePassword(String oldPassword, String newPassword);

}
