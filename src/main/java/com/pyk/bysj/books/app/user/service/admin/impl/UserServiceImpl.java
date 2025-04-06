package com.pyk.bysj.books.app.user.service.admin.impl;

import com.pyk.bysj.books.app.user.service.admin.UserService;
import com.pyk.bysj.books.enums.Role;
import com.pyk.bysj.books.enums.UserStatus;
import com.pyk.bysj.books.model.dto.UserDTO;
import com.pyk.bysj.books.model.entity.User;
import com.pyk.bysj.books.utils.ResponseData;

import java.util.List;

public class UserServiceImpl implements UserService {
  @Override
  public List<User> userList(UserDTO userDTO) {
    return List.of();
  }

  @Override
  public ResponseData changeRole(Integer id, Role role) {
    return null;
  }

  @Override
  public User getUserById(Integer id) {
    return null;
  }

  @Override
  public ResponseData changeStatus(Integer id, UserStatus status) {
    return null;
  }

  @Override
  public ResponseData addUser(User user) {
    return null;
  }
}
