package com.pyk.bysj.books;

import com.pyk.bysj.books.mapper.LoginMapper;
import com.pyk.bysj.books.model.entity.Login;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class BooksApplicationTests {


  @Autowired
  private LoginMapper loginMapper;
  @Autowired private PasswordEncoder passwordEncoder;

  @Test void contextLoads() {

    //导入一个用户
    Login sysUser = new Login();

    sysUser.setId(6);
    sysUser.setUserId(5);
    sysUser.setUsername("zhangsan");
    sysUser.setPassword(passwordEncoder.encode("123456"));
    loginMapper.insert(sysUser);

  }

}
