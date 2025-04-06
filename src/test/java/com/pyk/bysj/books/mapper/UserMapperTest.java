package com.pyk.bysj.books.mapper;

import com.pyk.bysj.books.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SpringBootTest
public class UserMapperTest {
  @Autowired UserMapper userMapper;
  @Test
  public void test() {
//    List<User> users = userMapper.selectList(null);
//    System.out.println(users);
    Map<String, Object> map = new HashMap<>();
    map.put("username", "admin");
    List<User> users = userMapper.selectByMap(map);
    users.forEach(System.out::println);



  }
}
