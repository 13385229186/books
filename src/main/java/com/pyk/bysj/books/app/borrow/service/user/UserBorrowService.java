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


}
