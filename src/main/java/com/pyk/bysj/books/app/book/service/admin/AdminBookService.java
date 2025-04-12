package com.pyk.bysj.books.app.book.service.admin;

import com.pyk.bysj.books.model.entity.Book;
import com.pyk.bysj.books.utils.ResponseData;

public interface AdminBookService {
  /**
   * 添加书籍基本信息
   * @param book 新增书籍
   * @return ResponseData
   */
  ResponseData addBook(Book book, Integer bookNumber);

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
   * 根据id修改实体书籍数量
   * @param id 书籍id
   * @param number 数量
   * @return ResponseData
   */
  ResponseData setBookNumber(Integer id, Integer number);






}
