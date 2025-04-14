package com.pyk.bysj.books.app.borrow.service.admin;

import com.pyk.bysj.books.enums.BorrowStatus;
import com.pyk.bysj.books.utils.ResponseData;

public interface AdminBorrowService {

  /**
   * 根据id修改借阅状态
   * @param id borrow_id
   * @param status 新的借阅状态
   * @return ResponseData
   */
  ResponseData setBorrowStatus(Long id, BorrowStatus status);

  /**
   * 根据id处理借阅
   * @param id borrow_id
   * @return ResponseData
   */
  ResponseData handleBorrow(Long id);

}
