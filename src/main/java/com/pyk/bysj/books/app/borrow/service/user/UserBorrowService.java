package com.pyk.bysj.books.app.borrow.service.user;

import com.pyk.bysj.books.model.dto.BorrowDTO;
import com.pyk.bysj.books.model.dto.ListQueryResult;
import com.pyk.bysj.books.model.dto.PageParam;
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

  /**
   * 获取当前用户的借阅信息列表
   * @param userId 用户id
   * @param pageParam 分页信息
   * @return ListQueryResult<BorrowDTO>
   */
  ListQueryResult<BorrowDTO> borrowListByUserId(Integer userId, PageParam pageParam);
}
