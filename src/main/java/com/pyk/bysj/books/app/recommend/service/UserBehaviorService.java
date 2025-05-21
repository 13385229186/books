package com.pyk.bysj.books.app.recommend.service;

import com.pyk.bysj.books.enums.BehaviorType;
import com.pyk.bysj.books.model.entity.UserBehavior;
import com.pyk.bysj.books.utils.ResponseData;

public interface UserBehaviorService {
  /**
   * 记录用户行为
   * @param userBehavior 用户行为实体类
   * @return ResponseData
   */
  ResponseData recordBehavior(UserBehavior userBehavior);
}
