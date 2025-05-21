package com.pyk.bysj.books.app.recommend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pyk.bysj.books.mapper.HotBookMapper;
import com.pyk.bysj.books.mapper.UserBehaviorMapper;
import com.pyk.bysj.books.model.entity.HotBook;
import com.pyk.bysj.books.model.entity.UserBehavior;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class HotBookCalculator {
  private final UserBehaviorMapper userBehaviorMapper;
  private final HotBookMapper hotBookMapper;
  // 时间衰减系数（每天衰减10%）
  private static final double DECAY_RATE = 0.1;

  // 每天凌晨2点执行
  @Scheduled(cron = "0 0 2 * * ?")
  public void calculateHotBooks() {
    log.info("--------开始计算热门书籍（带时间衰减）--------");

    try {
      // 获取原始行为数据（包含时间戳）
      List<UserBehavior> behaviors = userBehaviorMapper.selectList(
              new QueryWrapper<UserBehavior>()
                      .select("book_id", "weight", "created_at")
      );
      System.out.println("behaviors = " + behaviors);

      // 计算带时间衰减的热度分
      Map<Integer, Float> bookScores = calculateTimeDecayScores(behaviors);
      System.out.println("bookScores = " + bookScores);

      // 批量更新到数据库
      int affectedRows = batchUpdateHotBooks(bookScores);
      log.info("热门书籍更新完成，影响记录数：{}", affectedRows);

    } catch (Exception e) {
      log.error("热门书籍计算失败", e);
      throw new RuntimeException("热门书籍计算失败", e);
    }
  }

  /**
   * 计算带时间衰减的分数
   */
  private Map<Integer, Float> calculateTimeDecayScores(List<UserBehavior> behaviors) {
    // 分组计算原始总分（未衰减）
    Map<Integer, Double> rawScores = behaviors.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(
                    UserBehavior::getBookId,
                    Collectors.summingDouble(this::applyTimeDecay)
            ));

    // 归一化处理（0-100分）
    double maxScore = rawScores.values().stream()
            .max(Double::compare)
            .orElse(1.0);

    return rawScores.entrySet().stream()
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> (float)(entry.getValue() / maxScore * 100)
            ));
  }

  /**
   * 应用时间衰减和新书加成
   */
  private double applyTimeDecay(UserBehavior behavior) {
    // 计算时间衰减
    long daysOld = ChronoUnit.DAYS.between(
            behavior.getCreatedAt().toLocalDate(),
            LocalDate.now()
    );
    double decayFactor = Math.exp(-DECAY_RATE * daysOld);

    return behavior.getWeight() * decayFactor;
  }

  private int batchUpdateHotBooks(Map<Integer, Float> bookScores) {
    List<HotBook> updates = bookScores.entrySet().stream()
            .map(e -> new HotBook(e.getKey(), e.getValue()))
            .collect(Collectors.toList());
    return hotBookMapper.batchInsertOrUpdateByBookId(updates);
  }
}
