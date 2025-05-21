package com.pyk.bysj.books.app.recommend.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pyk.bysj.books.app.recommend.service.HotBookService;
import com.pyk.bysj.books.app.recommend.service.RecommendService;
import com.pyk.bysj.books.model.dto.BookWithHeatScoreDTO;
import com.pyk.bysj.books.model.dto.ListQueryResult;
import com.pyk.bysj.books.model.dto.PageParam;
import com.pyk.bysj.books.model.entity.Book;
import com.pyk.bysj.books.model.entity.HotBook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class HotBookServiceImpl implements HotBookService {
  private final RecommendService recommendService;

  @Override
  public ListQueryResult<BookWithHeatScoreDTO> hotBookList(PageParam pageParam) {
    // 获取分页数据
    Page<HotBook> hotBookPageResult = recommendService.getColdStartRecommendations(pageParam);
    List<HotBook> hotBooks = hotBookPageResult.getRecords();

    // 构建书籍基础数据
    ListQueryResult<Book> books = recommendService.buildRecommendationResult(hotBookPageResult);

    // 创建热度映射表
    Map<Integer, Float> heatScoreMap = hotBooks.stream()
            .collect(Collectors.toMap(
                    HotBook::getBookId,
                    HotBook::getHeatScore,
                    (oldVal, newVal) -> oldVal
            ));

    // 合并数据
    List<BookWithHeatScoreDTO> finalList = books.getData().stream()
            .map(book -> new BookWithHeatScoreDTO(
                    book,
                    heatScoreMap.getOrDefault(book.getId(), 0.0f) // 使用f明确表示float
            ))
            .toList();

    return new ListQueryResult<>(
            finalList,
            books.getSuccess(),
            books.getTotal(),
            books.getPages(),
            books.getCurrent()
    );
  }
}
