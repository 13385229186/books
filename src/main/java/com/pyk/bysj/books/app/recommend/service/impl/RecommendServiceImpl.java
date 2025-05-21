package com.pyk.bysj.books.app.recommend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pyk.bysj.books.app.book.service.user.UserBookService;
import com.pyk.bysj.books.app.recommend.service.RecommendService;
import com.pyk.bysj.books.mapper.*;
import com.pyk.bysj.books.model.dto.ListQueryResult;
import com.pyk.bysj.books.model.dto.PageParam;
import com.pyk.bysj.books.model.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RecommendServiceImpl implements RecommendService {
  private final UserBehaviorMapper userBehaviorMapper;
  private final BookMapper bookMapper;
  private final UserRecommendationMapper userRecommendationMapper;
  private final BookSimilarityMapper bookSimilarityMapper;
  private final UserBookService userBookService;
  private final HotBookMapper hotBookMapper;

  @Value("${app.recommendation.expire-seconds}")
  private Integer expireSeconds;

  private boolean generateRecommendations(Integer userId) {
    // 删除旧推荐
    userRecommendationMapper.delete(
            Wrappers.<UserRecommendation>lambdaQuery().eq(UserRecommendation::getUserId, userId)
    );
    // 获取用户历史行为书籍
    List<UserBehavior> userBehaviors = userBehaviorMapper.selectList(
            new LambdaQueryWrapper<UserBehavior>()
                    .eq(UserBehavior::getUserId, userId)
    );

    if(userBehaviors.isEmpty()){
      return true;
    }

    // 查询相似书籍（带权重）并保存
    userBehaviors.forEach(userBehavior -> {
      bookSimilarityMapper.selectList(
                new LambdaQueryWrapper<BookSimilarity>().eq(BookSimilarity::getBookId, userBehavior.getBookId())
        ).forEach(sim -> {
          float newScore = sim.getScore() * userBehavior.getWeight();
        UserRecommendation userBookRecommendation = userRecommendationMapper.selectOne(
                Wrappers.<UserRecommendation>lambdaQuery()
                        .eq(UserRecommendation::getUserId, userId)
                        .eq(UserRecommendation::getBookId, sim.getSimilarBookId())
        );
        if(userBookRecommendation != null) {
          userBookRecommendation.setScore(userBookRecommendation.getScore() + newScore);
          userRecommendationMapper.updateById(userBookRecommendation);
        }else{
          userRecommendationMapper.insert(new UserRecommendation(userId, sim.getSimilarBookId(), newScore));
        }
      });
    });
    return false;
  }

  /**
   * 检查用户推荐信息是否过期
   * @param createTime 用户推荐创建时间
   * @return boolean
   */
  private boolean isRecommendationExpired(LocalDateTime createTime) {
    return createTime.plusSeconds(expireSeconds).isBefore(LocalDateTime.now());
  }

  @Override
  public ListQueryResult<Book> getRecommendations(Integer userId, PageParam pageParam) {
    // 判断是否冷启动
    boolean isCold = checkColdStart(userId);

    // 获取分页结果（策略模式）
    Page<?> result = isCold ?
            getColdStartRecommendations(pageParam) :
            getPersonalizedRecommendations(userId, pageParam);

    // 统一处理结果
    return buildRecommendationResult(result);
  }

  private boolean checkColdStart(Integer userId) {
    List<UserRecommendation> cached = userRecommendationMapper.selectList(
            new LambdaQueryWrapper<UserRecommendation>()
                    .eq(UserRecommendation::getUserId, userId)
    );

    if (cached.isEmpty() || isRecommendationExpired(cached.get(0).getCreatedAt())) {
      log.info("重新生成推荐（用户ID：{}）", userId);
      return generateRecommendations(userId);
    }
    return false;
  }

  private Page<UserRecommendation> getPersonalizedRecommendations(Integer userId, PageParam pageParam) {
    return userRecommendationMapper.selectPage(
            new Page<>(pageParam.getCurrent(), pageParam.getPageSize()),
            new LambdaQueryWrapper<UserRecommendation>()
                    .eq(UserRecommendation::getUserId, userId)
                    .orderByDesc(UserRecommendation::getScore)
    );
  }

  @Override
  public Page<HotBook> getColdStartRecommendations(PageParam pageParam) {
    return hotBookMapper.selectPage(
            new Page<>(pageParam.getCurrent(), pageParam.getPageSize()),
            new LambdaQueryWrapper<HotBook>()
                    .orderByDesc(HotBook::getHeatScore)
    );
  }

  @Override
  public ListQueryResult<Book> buildRecommendationResult(Page<?> page) {
    // 统一提取bookId的逻辑
    List<Integer> bookIds = page.getRecords().stream()
            .map(record -> {
              if (record instanceof UserRecommendation) {
                return ((UserRecommendation) record).getBookId();
              } else if (record instanceof HotBook) {
                return ((HotBook) record).getBookId();
              }
              throw new IllegalStateException("未知的记录类型");
            })
            .collect(Collectors.toList());

    // 查询书籍详情
    List<Book> books = bookIds.isEmpty() ?
            Collections.emptyList() :
            bookMapper.selectByIds(bookIds);

    // 保持原始顺序
    Map<Integer, Book> bookMap = books.stream()
            .collect(Collectors.toMap(Book::getId, Function.identity()));

    List<Book> orderedBooks = bookIds.stream()
            .map(bookMap::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    return new ListQueryResult<>(
            orderedBooks,
            true,
            (int) page.getTotal(),
            (int) page.getPages(),
            (int) page.getCurrent()
    );
  }

}
