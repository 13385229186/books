package com.pyk.bysj.books.app.overView.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.pyk.bysj.books.app.overView.service.BorrowStatsService;
import com.pyk.bysj.books.mapper.BorrowMapper;
import com.pyk.bysj.books.mapper.CategoryMapper;
import com.pyk.bysj.books.model.entity.Book;
import com.pyk.bysj.books.model.entity.Borrow;
import com.pyk.bysj.books.model.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowStatsServiceImpl implements BorrowStatsService {
  private final BorrowMapper borrowMapper;
  private final CategoryMapper categoryMapper;

  // 分类借阅量统计
  @Override
  public List<Map<String, Object>> getBorrowCountByCategory() {
    // 获取所有分类基础信息
    List<Map<String, Object>> allCategories = categoryMapper.selectMaps(
            new QueryWrapper<Category>()
                    .select("id", "name")
    );

    // 获取有借阅记录的分类（30天内）
    List<Map<String, Object>> borrowedCategories = borrowMapper.selectJoinMaps(
            new MPJLambdaWrapper<Borrow>()
                    .selectAs(Category::getId, "category_id")
                    .selectAs(Category::getName, "name")
                    .selectCount(Borrow::getId, "borrow_count")
                    .leftJoin(Book.class, Book::getId, Borrow::getBookId)
                    .leftJoin(Category.class, Category::getId, Book::getCategoryId)
                    .ge(Borrow::getBorrowTime, LocalDateTime.now().minusDays(30))
                    .groupBy(Book::getCategoryId)
    );

    // 合并结果（补全0值）
    Map<Long, Map<String, Object>> borrowedMap = borrowedCategories.stream()
            .collect(Collectors.toMap(
                    m -> (Long) m.get("category_id"),
                    m -> {
                      m.put("borrow_count", ((Long) m.get("borrow_count")));
                      return m;
                    }
            ));

    return allCategories.stream()
            .map(category -> {
              Long categoryId = (Long) category.get("id");
              Map<String, Object> result = new HashMap<>();
              result.put("name", category.get("name"));
              result.put("borrow_count",
                      borrowedMap.containsKey(categoryId) ?
                              borrowedMap.get(categoryId).get("borrow_count") : 0L
              );
              return result;
            })
            .sorted((a, b) ->
                    ((Long) b.get("borrow_count")).compareTo((Long) a.get("borrow_count"))
            )
            .collect(Collectors.toList());
  }
}
