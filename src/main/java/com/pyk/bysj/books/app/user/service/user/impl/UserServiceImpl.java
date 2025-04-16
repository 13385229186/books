package com.pyk.bysj.books.app.user.service.user.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pyk.bysj.books.enums.Role;
import com.pyk.bysj.books.exception.general.SqlFailedException;
import com.pyk.bysj.books.mapper.LoginMapper;
import com.pyk.bysj.books.mapper.UserCreditMapper;
import com.pyk.bysj.books.mapper.UserMapper;
import com.pyk.bysj.books.model.LoginUserDetails;
import com.pyk.bysj.books.model.dto.LoginDTO;
import com.pyk.bysj.books.model.dto.UserUpdateDTO;
import com.pyk.bysj.books.model.entity.BookNumber;
import com.pyk.bysj.books.model.entity.Login;
import com.pyk.bysj.books.model.entity.User;
import com.pyk.bysj.books.app.user.service.user.UserService;
import com.pyk.bysj.books.model.entity.UserCredit;
import com.pyk.bysj.books.utils.RedisUtil;
import com.pyk.bysj.books.utils.ResponseData;
import com.pyk.bysj.books.utils.TokenBlacklist;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.pyk.bysj.books.utils.JwtTokenUtil;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
  @Autowired
  private AuthenticationManager authenticationManager;
  @Autowired
  private UserMapper userMapper;
  @Autowired
  private LoginMapper loginMapper;
  @Autowired
  private RedisUtil redisUtil;
  @Autowired
  private JwtTokenUtil jwtTokenUtil;
  @Autowired
  private UserCreditMapper userCreditMapper;
  @Autowired
  private TokenBlacklist tokenBlacklist;

  @Override
  public ResponseData login(LoginDTO loginDTO) {
    System.out.println("login");
    // 1 获取AuthenticationManager 对象 然后调用 authenticate() 方法
    // UsernamePasswordAuthenticationToken 实现了Authentication 接口
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
    Authentication authenticate = authenticationManager.authenticate(authenticationToken);

    System.out.println("进入service，" + authenticate);

    // 2 认证没通过 提示认证失败
    if (Objects.isNull(authenticate)) {
      return ResponseData.fail("认证失败用户信息不存在");
    }

    System.out.println("认证通过。。。。。。。。");


    // 认证通过 使用userid 生成jwt token令牌
    LoginUserDetails loginUserDetails = (LoginUserDetails) authenticate.getPrincipal();
    String token = jwtTokenUtil.generateToken(loginUserDetails.getUsername());

//    // 把用户信息存入到redis中
//    User loginUser = userMapper.selectById(login.getUserId());
//    redisUtil.set("login:" + loginUser.getId(), loginUser);

    // 返回token
    Map<String, Object> map = new HashMap<>();
    map.put("token", token);
    return ResponseData.success(map);
  }

  @Override
  public ResponseData register(String username, String encodedPassword) {
    // 检查username是否重复
    List<Login> usernameList = loginMapper.selectByMap(Map.of("username", username));
    if (!usernameList.isEmpty()) {
      return ResponseData.fail("用户名已存在！");
    }

    // 创建用户对象
    User user = new User(Role.USER);
    if (userMapper.insert(user) > 0) {
      // 创建登录对象
      Login login = new Login(user.getId(), username, encodedPassword);
      // 创建用户信用分
      UserCredit userCredit = new UserCredit(user.getId());
      if(loginMapper.insert(login) > 0 && userCreditMapper.insert(userCredit) > 0){
        return ResponseData.success();
      }
    }
    return ResponseData.fail("用户创建失败，请重试！");
  }

  @Override
  public ResponseData updateInfo(User user, UserUpdateDTO updateDTO, String token) {
    // 检查user是否需要更新，并设置更新项
    boolean isUserToUpdated = handleUserUpdates(user, updateDTO);

    // 检查login是否需要更新
    boolean isLoginToUpdated = false;
    Login login = null;
    if (StringUtils.isNotBlank(updateDTO.getUsername()) || StringUtils.isNotBlank(updateDTO.getPassword())) {
      // 根据userId获取Login
      login = loginMapper.selectOne(
              Wrappers.<Login>lambdaQuery().eq(Login::getUserId, user.getId())
      );
      // 设置更新项
      isLoginToUpdated = handleLoginUpdates(login, updateDTO);
    }

    // 执行更新
    return executeUpdates(user, login, isUserToUpdated, isLoginToUpdated, updateDTO, token);
  }

  /**
   * 根据需要更新user
   * @param user 原始user
   * @param dto 更新项
   * @return boolean 是否需要更新
   */
  private boolean handleUserUpdates(User user, UserUpdateDTO dto) {
    boolean isToUpdated = false;

    // avatar
    if (StringUtils.isNotBlank(dto.getAvatar())) {
      user.setAvatar(dto.getAvatar());
      isToUpdated = true;
    }

    // name
    if (StringUtils.isNotBlank(dto.getName())) {
      user.setName(dto.getName());
      isToUpdated = true;
    }

    // phone
    if (StringUtils.isNotBlank(dto.getPhone())) {
      validatePhoneUniqueness(dto.getPhone(), user.getId());
      user.setPhone(dto.getPhone());
      isToUpdated = true;
    }

    return isToUpdated;
  }

  /**
   * 根据需要更新login
   * @param login 原始login
   * @param dto 更新项
   * @return boolean 是否需要更新
   */
  private boolean handleLoginUpdates(Login login, UserUpdateDTO dto) {
    boolean isToUpdated = false;

    // username
    if (StringUtils.isNotBlank(dto.getUsername())) {
      login.setUsername(dto.getUsername());
      isToUpdated = true;
    }

    // password
    if (StringUtils.isNotBlank(dto.getPassword())) {
      login.setPassword(dto.getPassword());
      isToUpdated = true;
    }

    return isToUpdated;
  }

  /**
   * 检验电话号码是否会重复
   * @param phone 电话号码
   * @param excludeUserId 排除user
   */
  private void validatePhoneUniqueness(String phone, Integer excludeUserId) {
    long count = userMapper.selectCount(
            Wrappers.<User>lambdaQuery()
                    .eq(User::getPhone, phone)
                    .ne(excludeUserId != null, User::getId, excludeUserId)
    );

    if (count > 0) {
      throw new IllegalArgumentException("该手机号已被使用");
    }
  }

  /**
   * 据需要执行更新
   * @param user 用户
   * @param updateUser 是否需要更新user
   * @param updateLogin 是否需要更新login
   * @return ResponseData
   */
  private ResponseData executeUpdates(User user, Login login, boolean updateUser, boolean updateLogin, UserUpdateDTO dto, String token) {
    boolean isUserUpdateSuccess = !updateUser;
    boolean isLoginUpdateSuccess = !updateLogin;

    if (updateUser) {
      int userUpdateCount = userMapper.updateById(user);
      if (userUpdateCount > 0) {
        isUserUpdateSuccess = true;
      }
    }

    if (updateLogin) {
      int loginUpdateCount = loginMapper.updateById(login);
      if (loginUpdateCount > 0) {
        isLoginUpdateSuccess = true;
      }
      if (StringUtils.isNotBlank(dto.getPassword())) {
        // 使用户当前token失效
        tokenBlacklist.addToBlacklist(token);
      }
    }

    if (isUserUpdateSuccess && isLoginUpdateSuccess) {
      return ResponseData.success();
    }
    throw new SqlFailedException("更新失败");
  }

  @Override
  public ResponseData updatePassword(String oldPassword, String newPassword) {
    return null;
  }
}
