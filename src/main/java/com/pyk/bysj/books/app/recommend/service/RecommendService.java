package com.pyk.bysj.books.app.recommend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pyk.bysj.books.model.dto.ListQueryResult;
import com.pyk.bysj.books.model.dto.PageParam;
import com.pyk.bysj.books.model.entity.Book;
import com.pyk.bysj.books.model.entity.HotBook;

public interface RecommendService {
  /**
   * 获取推荐书籍列表
   * @param userId 用户id
   * @param pageParam 分页参数
   * @return ListQueryResult<Book>
   */
  ListQueryResult<Book> getRecommendations(Integer userId, PageParam pageParam);

  /**
   * 获取热门书籍表分页结果
   * @param pageParam 分页参数
   * @return Page<HotBook>
   */
  Page<HotBook> getColdStartRecommendations(PageParam pageParam);

  /**
   * 获取推荐/热门书籍相关信息列表
   * @param page 分页结果
   * @return ListQueryResult<Book>
   */
  ListQueryResult<Book> buildRecommendationResult(Page<?> page);
}
