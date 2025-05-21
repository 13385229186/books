package com.pyk.bysj.books.app.user.service.admin;

import com.pyk.bysj.books.enums.Role;
import com.pyk.bysj.books.enums.UserStatus;
import com.pyk.bysj.books.model.dto.AddUserDTO;
import com.pyk.bysj.books.model.dto.ListQueryResult;
import com.pyk.bysj.books.model.dto.PageParam;
import com.pyk.bysj.books.model.dto.UserDTO;
import com.pyk.bysj.books.model.entity.User;
import com.pyk.bysj.books.utils.ResponseData;

import java.util.List;
import java.util.Map;

public interface AdminUserService {
  /**
   * 获取用户列表，支持分页、条件筛选
   * @param userMap 筛选条件
   * @param pageParam 分页参数
   * @return 用户列表
   */
  ListQueryResult<UserDTO> userList(Map<String, Object> userMap, PageParam pageParam);

  /**
   * 修改用户角色权限
   * @param id 用户id
   * @param role 目标修改角色
   * @return ResponseData
   */
  ResponseData changeRole(Integer id, Role role);

  /**
   * 根据用户id获取用户详情
   * @param id 用户id
   * @return UserDTO用户详情
   */
  UserDTO getUserById(Integer id);

  /**
   * 根据用户id修改用户账户状态
   * @param id 用户id
   * @param status 目标账户状态
   * @return ResponseData
   */
  ResponseData changeStatus(Integer id, UserStatus status);

  /**
   * 添加用户
   * @param addUserDTO 新增用户
   * @return ResponseData
   */
  ResponseData addUser(AddUserDTO addUserDTO);

}
