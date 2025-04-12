package com.pyk.bysj.books.app.book.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pyk.bysj.books.app.book.service.user.UserBookService;
import com.pyk.bysj.books.enums.BookStatus;
import com.pyk.bysj.books.mapper.BookMapper;
import com.pyk.bysj.books.mapper.BookNumberMapper;
import com.pyk.bysj.books.model.dto.BookDTO;
import com.pyk.bysj.books.model.dto.ListQueryResult;
import com.pyk.bysj.books.model.dto.PageParam;
import com.pyk.bysj.books.model.entity.Book;
import com.pyk.bysj.books.model.entity.BookNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Service
public class UserBookServiceImpl implements UserBookService {
  private static final Logger log = LoggerFactory.getLogger(UserBookServiceImpl.class);
  private final BookMapper bookMapper;
  private final BookNumberMapper bookNumberMapper;

  public UserBookServiceImpl(BookMapper bookMapper, BookNumberMapper bookNumberMapper) {
    this.bookMapper = bookMapper;
    this.bookNumberMapper = bookNumberMapper;
  }

  @Override
  public ListQueryResult<Book> bookList(Map<String, Object> bookMap, PageParam pageParam) {
    // 开启分页查询
    Page<Book> page = new Page<>(pageParam.getCurrent(), pageParam.getPageSize());
    // 筛选条件Wrapper
    LambdaQueryWrapper<Book> wrapper = Wrappers.lambdaQuery();
    bookMap.forEach((key, value) -> {
      if (StringUtils.isNotBlank(key) && value != null) {
        // 根据参数名动态添加条件
        switch (key) {
          // 精确匹配字段
          case "id":
            wrapper.eq(Book::getId, Integer.parseInt(value.toString()));
            break;
          case "isbn":
            wrapper.eq(Book::getIsbn, value.toString());
            break;
          case "categoryId":
            wrapper.eq(Book::getCategoryId, Integer.parseInt(value.toString()));
            break;
          case "status":
            wrapper.eq(Book::getStatus, BookStatus.valueOf(value.toString()));
            break;

          // 模糊搜索字段
          case "title":
            wrapper.like(Book::getTitle, "%" + value + "%");
            break;
          case "author":
            wrapper.like(Book::getAuthor, "%" + value + "%");
            break;
          case "press":
            wrapper.like(Book::getPress, "%" + value + "%");
            break;
          case "intro":
            wrapper.like(Book::getIntro, "%" + value + "%");
            break;

          // 默认处理（防止恶意参数）
          default:
            log.warn("未知查询参数: {}={}", key, value);
            throw new IllegalArgumentException("参数处理失败: " + key);
        }
      }
    });

    Page<Book> result = bookMapper.selectPage(page, wrapper);

    // 获取分页数据
    List<Book> records = result.getRecords(); // 当前页数据

    return new ListQueryResult<>(
            records,
            true,
            (int)result.getTotal(),
            (int)result.getPages(),
            (int)result.getCurrent());
  }

  @Override
  public Book getBookById(Integer id) {
    return bookMapper.selectById(id);
  }

  @Override
  public int getBookNumberById(Integer id) {
    List<BookNumber> bookNumbers = bookNumberMapper.selectByMap(Map.of("book_id", id));
    if (!bookNumbers.isEmpty() && bookNumbers.get(0) != null) {
      return bookNumbers.get(0).getNumber();
    }
    return 0;
  }
}
