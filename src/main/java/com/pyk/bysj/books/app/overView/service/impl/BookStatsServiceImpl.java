package com.pyk.bysj.books.app.overView.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pyk.bysj.books.app.overView.service.BookStatsService;
import com.pyk.bysj.books.enums.BookStatus;
import com.pyk.bysj.books.enums.BorrowStatus;
import com.pyk.bysj.books.mapper.BookMapper;
import com.pyk.bysj.books.mapper.BookNumberMapper;
import com.pyk.bysj.books.mapper.BorrowMapper;
import com.pyk.bysj.books.model.dto.overView.BookOverviewVO;
import com.pyk.bysj.books.model.entity.Book;
import com.pyk.bysj.books.model.entity.BookNumber;
import com.pyk.bysj.books.model.entity.Borrow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookStatsServiceImpl implements BookStatsService {
  private final BookMapper bookMapper;
  private final BookNumberMapper bookNumberMapper;
  private final BorrowMapper borrowMapper;

  public Map<String, Object> getBookOverview() {
    Map<String, Object> result = new HashMap<>();

    // 书籍总量
    result.put("total", bookMapper.selectCount(null));

    // 电子书统计
    result.put("ebookStats", getEbookStats());

    // 纸质书可借阅统计
    result.put("physicalStats", getPhysicalAvailableStats());

    // 当日借阅量
    Long todayBorrows = borrowMapper.selectCount(
            new QueryWrapper<Borrow>()
                    .ge("borrow_time", LocalDate.now())
                    .le("borrow_time", LocalDate.now().plusDays(1))
    );
    result.put("todayBorrows", todayBorrows);

    // 逾期未归还
    Long overdue = borrowMapper.selectCount(
            new QueryWrapper<Borrow>()
                    .lt("due_time", LocalDateTime.now())
                    .isNull("return_time")
                    .and(wrapper -> wrapper.in("status", BorrowStatus.BORROWED, BorrowStatus.OVERDUE))
    );
    result.put("overdue", overdue);

    return result;
  }

  private Map<String, Object> getEbookStats() {
    // 使用QueryWrapper统计电子书
    QueryWrapper<Book> ebookWrapper = new QueryWrapper<Book>()
            .isNotNull("ebook");

    Long ebookCount = bookMapper.selectCount(ebookWrapper);
    Long totalCount = bookMapper.selectCount(null);
    double percentage = totalCount > 0 ?
            Math.round(ebookCount * 10000.0 / totalCount) / 100.0 : 0;

    return Map.of(
            "count", ebookCount,
            "percentage", percentage
    );
  }

  private Map<String, Object> getPhysicalAvailableStats() {
    // 1. 先查询所有书籍
    QueryWrapper<Book> physicalWrapper = new QueryWrapper<>();
    List<Book> physicalBooks = bookMapper.selectList(physicalWrapper);

    // 2. 查询可借阅数量
    int availableCount = 0;
    for (Book book : physicalBooks) {
      // 检查状态为AVAILABLE
      if (BookStatus.AVAILABLE.equals(book.getStatus())) {
        // 检查book_number表中的数量
        QueryWrapper<BookNumber> numberWrapper = new QueryWrapper<BookNumber>()
                .eq("book_id", book.getId())
                .gt("number", 0);
        if (bookNumberMapper.exists(numberWrapper)) {
          availableCount++;
        }
      }
    }

    // 3. 计算占比（分母是所有书籍）
    double percentage = physicalBooks.isEmpty() ? 0 :
            Math.round(availableCount * 10000.0 / physicalBooks.size()) / 100.0;

    return Map.of(
            "count", availableCount,
            "percentage", percentage
    );
  }
}