package com.pyk.bysj.books.security;

import com.pyk.bysj.books.mapper.LoginMapper;
import com.pyk.bysj.books.mapper.UserMapper;
import com.pyk.bysj.books.model.LoginUserDetails;
import com.pyk.bysj.books.model.entity.Login;
import com.pyk.bysj.books.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired
  private LoginMapper loginMapper;
  @Autowired
  private UserMapper userMapper;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    System.out.println("进入loadUserByUsername");

    // 通过username查询login对象
    Map<String, Object> map = new HashMap<>();
    map.put("username", username);
    List<Login> logins = loginMapper.selectByMap(map);
    if (logins == null || logins.isEmpty()) {
      throw new UsernameNotFoundException("用户不存在");
    }
    Login login = logins.get(0);
    LoginUserDetails loginUserDetails = new LoginUserDetails(userMapper.selectById(login.getUserId()), login, "ROLE_" + userMapper.selectById(login.getUserId()).getRole());
    System.out.println("loginUserDetails = " + loginUserDetails);
    return loginUserDetails;
  }
}
