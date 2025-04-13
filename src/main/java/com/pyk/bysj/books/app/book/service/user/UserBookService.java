package com.pyk.bysj.books.app.book.service.user;

import com.pyk.bysj.books.model.dto.BookDTO;
import com.pyk.bysj.books.model.dto.ListQueryResult;
import com.pyk.bysj.books.model.dto.PageParam;
import com.pyk.bysj.books.model.entity.Book;
import com.pyk.bysj.books.model.entity.User;
import com.pyk.bysj.books.utils.ResponseData;

import java.util.List;
import java.util.Map;

public interface UserBookService {
  /**
   * 获取书籍列表，支持分页、条件筛选
   * @return 书籍列表
   */
  ListQueryResult<Book> bookList(Map<String, Object> bookMap, PageParam pageParam);

  /**
   * 根据书籍id获取书籍详细信息
   * @param id 书籍id
   * @return Book书籍详细信息
   */
  Book getBookById(Integer id);

  /**
   * 根据书籍id获取实体书籍数量
   * @param id 书籍id
   * @return int实体书籍数量
   */
  int getBookNumberById(Integer id);



}
