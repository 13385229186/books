package com.pyk.bysj.books.app.borrow.service.admin.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pyk.bysj.books.app.borrow.service.admin.AdminBorrowService;
import com.pyk.bysj.books.enums.BorrowStatus;
import com.pyk.bysj.books.enums.ViolationType;
import com.pyk.bysj.books.exception.borrow.BorrowStatusException;
import com.pyk.bysj.books.exception.general.OperationFailedException;
import com.pyk.bysj.books.exception.general.SqlFailedException;
import com.pyk.bysj.books.mapper.BorrowMapper;
import com.pyk.bysj.books.mapper.UserCreditMapper;
import com.pyk.bysj.books.mapper.ViolationRecordMapper;
import com.pyk.bysj.books.model.entity.Borrow;
import com.pyk.bysj.books.model.entity.UserCredit;
import com.pyk.bysj.books.model.entity.ViolationRecord;
import com.pyk.bysj.books.utils.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Wrapper;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Transactional
public class AdminBorrowServiceImpl implements AdminBorrowService {
  private final BorrowMapper borrowMapper;
  private final ViolationRecordMapper violationRecordMapper;
  private final UserCreditMapper userCreditMapper;

  @Autowired
  public AdminBorrowServiceImpl(final BorrowMapper borrowMapper, ViolationRecordMapper violationRecordMapper, UserCreditMapper userCreditMapper) {
    this.borrowMapper = borrowMapper;
    this.violationRecordMapper = violationRecordMapper;
    this.userCreditMapper = userCreditMapper;
  }

  @Override
  public ResponseData setBorrowStatus(Long id, BorrowStatus status) {
    Borrow borrow = borrowMapper.selectById(id);

    // 根据不同目标状态进行处理
    ResponseData responseData = ResponseData.success();
    if (ViolationType.canConvertByName(status.name())){
      // 判断是否属于违规行为状态EXPIRED、OVERDUE、LOST
      responseData = handleViolationStatus(borrow, ViolationType.valueOf(status.name()));
    }else{
      // 其余属于正常借阅流程状态 CANCELLED、BORROWED、RETURNED
      responseData = handleNormalStatus(borrow, status);
    }
    if(responseData.getCode() != 200){
      return responseData;
    }

    // 修改借阅状态
    borrow.setStatus(status);
    int i = borrowMapper.updateById(borrow);
    if (i > 0) {
      return ResponseData.success();
    }
    throw new SqlFailedException("借阅状态修改失败");
  }

  /**
   * 处理违规行为
   * @param borrow 借阅记录
   * @param violationType 违规类型
   * @return ResponseData
   */
  private ResponseData handleViolationStatus(Borrow borrow, ViolationType violationType) {
    int gapScore = 0; // 即将扣除的信用分
    // 判断违规类型
    switch (violationType) {
      case EXPIRED -> {
        if(borrow.getStatus() != BorrowStatus.APPLIED){
          throw new BorrowStatusException("只有申请中的借阅记录可标记为已过期");
        }
        gapScore = 10;
      }
      case OVERDUE -> {
        if(borrow.getStatus() != BorrowStatus.BORROWED){
          throw new BorrowStatusException("只有借阅中的借阅记录可标记为已逾期");
        }
        gapScore = 20;
      }
      case LOST -> {
        if(borrow.getStatus() != BorrowStatus.BORROWED && borrow.getStatus() != BorrowStatus.OVERDUE){
          throw new BorrowStatusException("只有借阅中或已逾期的借阅记录可标记为已丢失");
        }
        gapScore = 40;
      }
    }

    // 生成违规记录
    ViolationRecord violationRecord = new ViolationRecord(borrow.getUserId(), borrow.getId(), violationType);
    int i1 = violationRecordMapper.insert(violationRecord);
    if (i1 <= 0) {
      throw new SqlFailedException("违规记录生成失败");
    }

    // 相应扣除信用分
    return setCreditScore(borrow.getUserId(), gapScore, false);
  }

  /**
   * 处理正常借阅流程状态
   * @param borrow 借阅记录
   * @param status 借阅状态
   * @return ResponseData
   */
  private ResponseData handleNormalStatus(Borrow borrow, BorrowStatus status) {
    LocalDateTime now = LocalDateTime.now();
    switch (status){
      case CANCELLED -> {
        if(borrow.getStatus() != BorrowStatus.APPLIED){
          throw new BorrowStatusException("只有申请中的借阅记录可标记为已取消");
        }
      }
      case BORROWED -> {
        if(borrow.getStatus() != BorrowStatus.APPLIED){
          throw new BorrowStatusException("只有申请中的借阅记录可标记为借阅中");
        }
        borrow.setBorrowTime(now);
        borrow.setDueTime(now.plusDays(borrow.getBorrowDays()));
      }
      case RETURNED -> {
        if(borrow.getStatus() != BorrowStatus.BORROWED && borrow.getStatus() != BorrowStatus.OVERDUE){
          throw new BorrowStatusException("只有借阅中或已逾期的借阅记录可标记为已归还");
        }
        borrow.setReturnTime(now);
        if(borrow.getStatus() == BorrowStatus.BORROWED){
          setCreditScore(borrow.getUserId(), 5, true);
        }
      }
    }
    return ResponseData.success();
  }

  /**
   * 信用分变化
   * @param userId 用户id
   * @param gapScore 信用分变化量
   * @param isPlus true为加分，false为减分
   * @return ResponseData
   */
  private ResponseData setCreditScore(Integer userId, Integer gapScore, Boolean isPlus){
    UserCredit userCredit = userCreditMapper.selectOne(
            Wrappers.<UserCredit>lambdaQuery()
                    .eq(UserCredit::getUserId, userId)
    );
    Integer score =  isPlus ?
            Math.min(userCredit.getCreditScore() + gapScore, 100) :
            Math.max(userCredit.getCreditScore() - gapScore, 0);
    userCredit.setCreditScore(score);
    int i = userCreditMapper.updateById(userCredit);

    if (i > 0) {
      return ResponseData.success();
    }
    throw new SqlFailedException("信用分变化失败");
  }

  @Override
  public ResponseData handleBorrow(Long id) {
    return setBorrowStatus(id, BorrowStatus.BORROWED);
  }




}
