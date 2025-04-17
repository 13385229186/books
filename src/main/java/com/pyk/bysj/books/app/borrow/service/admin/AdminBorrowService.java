package com.pyk.bysj.books.app.borrow.service.admin;

import com.pyk.bysj.books.enums.BorrowStatus;
import com.pyk.bysj.books.model.dto.*;
import com.pyk.bysj.books.utils.ResponseData;

import java.util.Map;

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

  /**
   * 获取借阅信息列表，支持分页、条件筛选
   * @param borrowMap 筛选条件
   * @param pageParam 分页参数
   * @return 借阅信息列表
   */
  ListQueryResult<BorrowDTO> borrowList(Map<String, Object> borrowMap, PageParam pageParam);

  /**
   * 获取违规信息列表，支持分页、条件筛选
   * @param violationMap 筛选条件
   * @param pageParam 分页参数
   * @return 违规信息列表
   */
  ListQueryResult<ViolationDTO> violationList(Map<String, Object> violationMap, PageParam pageParam);


}
