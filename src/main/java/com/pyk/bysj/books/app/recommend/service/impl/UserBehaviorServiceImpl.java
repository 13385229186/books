package com.pyk.bysj.books.app.recommend.service.impl;

import com.pyk.bysj.books.app.recommend.service.UserBehaviorService;
import com.pyk.bysj.books.enums.BehaviorType;
import com.pyk.bysj.books.mapper.UserBehaviorMapper;
import com.pyk.bysj.books.model.entity.UserBehavior;
import com.pyk.bysj.books.utils.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Transactional
@Service
public class UserBehaviorServiceImpl implements UserBehaviorService {
  private final UserBehaviorMapper userBehaviorMapper;

  @Autowired
  public UserBehaviorServiceImpl(UserBehaviorMapper userBehaviorMapper) {
    this.userBehaviorMapper = userBehaviorMapper;
  }

  @Override
  public ResponseData recordBehavior(UserBehavior userBehavior) {
    int result = userBehaviorMapper.insert(userBehavior);
    if (result > 0) {
      return ResponseData.success();
    }
    return ResponseData.fail("行为记录失败");
  }
}
