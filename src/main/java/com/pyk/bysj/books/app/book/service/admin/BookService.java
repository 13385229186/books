package com.pyk.bysj.books.app.book.service.admin;

import com.pyk.bysj.books.model.dto.BookDTO;
import com.pyk.bysj.books.model.entity.Book;
import com.pyk.bysj.books.utils.ResponseData;

import java.util.List;

public interface BookService {

  /**
   * 获取书籍列表，支持分页、条件筛选
   * @return 书籍列表
   */
  List<Book> bookList(BookDTO bookDTO);

  /**
   * 添加书籍
   * @param book 新增书籍
   * @return ResponseData
   */
  ResponseData addBook(Book book);

  /**
   * 根据id修改书籍信息
   * @param book 书籍信息修改内容
   * @return ResponseData
   */
  ResponseData updateBook(Book book);

  /**
   * 根据id删除书籍
   * @param id 书籍id
   * @return ResponseData
   */
  ResponseData deleteBook(Integer id);

  /**
   * 根据书籍id获取书籍详细信息
   * @param id 书籍id
   * @return Book书籍详细信息
   */
  Book getBookById(Integer id);







}
