package com.pyk.bysj.books.app.borrow.service.user;

import com.pyk.bysj.books.model.entity.User;
import com.pyk.bysj.books.utils.ResponseData;

public interface UserBorrowService {

  /**
   * 用户申请借阅实体书
   * @param user 用户
   * @param bookId 书籍id
   * @return ResponseData
   */
  ResponseData borrowBook(User user, Integer bookId, Integer borrowDays);

  /**
   * 用户取消借阅申请
   * @param user 用户
   * @param borrowId 借阅记录id
   * @return ResponseData
   */
  ResponseData cancelBorrowBook(User user, Long borrowId);

}
