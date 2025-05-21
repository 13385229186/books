package com.pyk.bysj.books.app.overView.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pyk.bysj.books.app.overView.service.UserStatsService;
import com.pyk.bysj.books.mapper.UserCreditMapper;
import com.pyk.bysj.books.mapper.UserMapper;
import com.pyk.bysj.books.model.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserStatsServiceImpl implements UserStatsService {
  private final UserMapper userMapper;
  private final UserCreditMapper userCreditMapper;

  // 近7天注册用户统计
  @Override
  public List<Map<String, Object>> getRecentRegistrations() {
    return userMapper.selectMaps(new QueryWrapper<User>()
            .select("DATE(created_at) as date", "COUNT(*) as count")
            .ge("created_at", LocalDate.now().minusDays(7))
            .groupBy("DATE(created_at)")
            .orderByAsc("DATE(created_at)")
    );
  }

  // 用户信用分布（三个区间）
  @Override
  public Map<String, Long> getCreditDistribution() {
    List<Map<String, Object>> raw = userCreditMapper.selectMaps(
            new QueryWrapper<UserCredit>()
                    .select("CASE " +
                                    "WHEN credit_score >= 80 THEN '优秀' " +
                                    "WHEN credit_score >= 50 THEN '良好' " +
                                    "ELSE '待提升' END as level",
                            "COUNT(*) as count")
                    .groupBy("level")
    );

    return raw.stream().collect(Collectors.toMap(
            m -> (String) m.get("level"),
            m -> (Long) m.get("count")
    ));
  }


}
