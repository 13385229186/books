package com.pyk.bysj.books.app.user.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.pyk.bysj.books.app.user.service.admin.AdminUserService;
import com.pyk.bysj.books.enums.BookStatus;
import com.pyk.bysj.books.enums.Role;
import com.pyk.bysj.books.enums.UserStatus;
import com.pyk.bysj.books.exception.general.NotExistException;
import com.pyk.bysj.books.exception.general.SqlFailedException;
import com.pyk.bysj.books.mapper.UserMapper;
import com.pyk.bysj.books.model.dto.ListQueryResult;
import com.pyk.bysj.books.model.dto.PageParam;
import com.pyk.bysj.books.model.dto.UserDTO;
import com.pyk.bysj.books.model.entity.Book;
import com.pyk.bysj.books.model.entity.Login;
import com.pyk.bysj.books.model.entity.User;
import com.pyk.bysj.books.model.entity.UserCredit;
import com.pyk.bysj.books.utils.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {
  private final UserMapper userMapper;

  public AdminUserServiceImpl(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  @Override
  public ListQueryResult<UserDTO> userList(Map<String, Object> userMap, PageParam pageParam) {
    // 开启分页查询
    Page<UserDTO> page = new Page<>(pageParam.getCurrent(), pageParam.getPageSize());
    // 筛选条件Wrapper
    MPJLambdaWrapper<User> wrapper = JoinWrappers.lambda(User.class);
    wrapper.selectAll();
    userMap.forEach((key, value) -> {
      if (StringUtils.isNotBlank(key) && value != null) {
        // 根据参数名动态添加条件
        switch (key) {
          // 精确匹配字段
          case "id":
            wrapper.eq(User::getId, Integer.parseInt(value.toString()));
            break;
          case "name":
            wrapper.eq(User::getName, value.toString());
            break;
          case "phone":
            wrapper.eq(User::getPhone, value.toString());
            break;
          case "role":
            wrapper.eq(User::getRole, Role.fromValue(value.toString()));
            break;
          case "credit_score":
            wrapper.select(UserCredit::getCreditScore)
                    .innerJoin(UserCredit.class, UserCredit::getUserId, User::getId)
                    .eq(UserCredit::getCreditScore, Integer.parseInt(value.toString()));
            break;

          // 模糊搜索字段
          case "username":
            wrapper.select(Login::getUsername)
                    .innerJoin(Login.class, Login::getUserId, User::getId)
                    .like(Login::getUsername, "%" + value + "%");
            break;

          // 默认处理（防止恶意参数）
          default:
            log.warn("未知查询参数: {}={}", key, value);
            throw new IllegalArgumentException("参数处理失败: " + key);
        }
      }
    });

    Page<UserDTO> result = userMapper.selectJoinPage(page, UserDTO.class, wrapper);

    // 获取分页数据
    List<UserDTO> records = result.getRecords(); // 当前页数据

    return new ListQueryResult<>(
            records,
            true,
            (int)result.getTotal(),
            (int)result.getPages(),
            (int)result.getCurrent());
  }

  @Override
  public ResponseData changeRole(Integer id, Role role) {
    // 检查用户是否存在
    User user = userMapper.selectById(id);
    if (user == null) {
      throw new NotExistException("用户不存在");
    }
    // 修改角色
    user.setRole(role);
    int i = userMapper.updateById(user);
    if (i == 1) {
      return ResponseData.success();
    }
    throw new SqlFailedException("角色修改失败");
  }

  @Override
  public UserDTO getUserById(Integer id) {
    MPJLambdaWrapper<User> wrapper = JoinWrappers.lambda(User.class);
    wrapper.selectAll()
            .select(UserCredit::getCreditScore)
            .innerJoin(UserCredit.class, UserCredit::getUserId, User::getId)
            .select(Login::getUsername)
            .innerJoin(Login.class, Login::getUserId, User::getId)
            .eq(User::getId, id);

    UserDTO userDTO = userMapper.selectJoinOne(UserDTO.class, wrapper);
    if (userDTO == null) {
      throw new NotExistException("用户不存在");
    }
    return userDTO;
  }

  @Override
  public ResponseData changeStatus(Integer id, UserStatus status) {
    // 检查用户是否存在
    User user = userMapper.selectById(id);
    if (user == null) {
      throw new NotExistException("用户不存在");
    }
    // 修改用户状态
    user.setStatus(status);
    int i = userMapper.updateById(user);
    if (i == 1) {
      return ResponseData.success();
    }
    throw new SqlFailedException("用户状态修改失败");
  }

  @Override
  public ResponseData addUser(User user) {
    return null;
  }
}
