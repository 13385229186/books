package com.pyk.bysj.books.app.user.service.user.impl;

import com.pyk.bysj.books.enums.Role;
import com.pyk.bysj.books.mapper.LoginMapper;
import com.pyk.bysj.books.mapper.UserCreditMapper;
import com.pyk.bysj.books.mapper.UserMapper;
import com.pyk.bysj.books.model.LoginUserDetails;
import com.pyk.bysj.books.model.dto.LoginDTO;
import com.pyk.bysj.books.model.entity.Login;
import com.pyk.bysj.books.model.entity.User;
import com.pyk.bysj.books.app.user.service.user.UserService;
import com.pyk.bysj.books.model.entity.UserCredit;
import com.pyk.bysj.books.utils.RedisUtil;
import com.pyk.bysj.books.utils.ResponseData;
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

  @Override
  @Transactional
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
  @Transactional
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
  public ResponseData updateInfo(User user, String name, String phone) {
    // 检查phone是否会重复
    List<User> phoneList = userMapper.selectByMap(Map.of("phone", phone));
    if (!phoneList.isEmpty() && !user.equals(phoneList.get(0))) {
      return ResponseData.fail("请检查电话号码是否填写正确！");
    }

    user.setName(name);
    user.setPhone(phone);
    if(userMapper.updateById(user) > 0){
      return ResponseData.success();
    } else {
      return ResponseData.fail("用户信息更新失败！");
    }
  }

  @Override
  public ResponseData updatePassword(String oldPassword, String newPassword) {
    return null;
  }
}
