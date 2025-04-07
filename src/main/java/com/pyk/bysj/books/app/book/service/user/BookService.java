package com.pyk.bysj.books.app.book.service.user;

import com.pyk.bysj.books.model.dto.BookDTO;
import com.pyk.bysj.books.model.entity.Book;

import java.util.List;

public interface BookService {
  /**
   * 获取书籍列表，支持分页、条件筛选
   * @return 书籍列表
   */
  List<Book> bookList(BookDTO bookDTO);

  /**
   * 根据书籍id获取书籍详细信息
   * @param id 书籍id
   * @return Book书籍详细信息
   */
  Book getBookById(Integer id);
}
