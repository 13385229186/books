package com.pyk.bysj.books.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pyk.bysj.books.model.entity.UserBehavior;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserBehaviorMapper extends BaseMapper<UserBehavior> {
  // 在UserBehaviorMapper.java中添加
  @Select("SELECT user_id, book_id, weight FROM user_behavior " +
          "WHERE user_id = #{userId} AND created_at >= DATE_SUB(NOW(), INTERVAL #{days} DAY)")
  List<UserBehavior> selectRecentBehaviors(@Param("userId") Integer userId, @Param("days") Integer days);
}
