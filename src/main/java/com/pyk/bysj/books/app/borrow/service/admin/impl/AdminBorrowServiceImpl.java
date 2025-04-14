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
    Borrow borrow = new Borrow();
    borrow.setId(id);
    borrow.setStatus(status);
    int i = borrowMapper.updateById(borrow);
    if (i > 0) {
      // 判断是否属于违规行为状态EXPIRED、OVERDUE、LOST
      if (ViolationType.canConvertByName(status.name())){
        return handleViolation(borrowMapper.selectById(id), ViolationType.valueOf(status.name()));
      }


      // 正常借阅流程状态CANCELLED、BORROWED、RETURNED


      return ResponseData.success();
    }
    throw new SqlFailedException("状态修改失败");
  }

  /**
   * 处理违规行为
   * @param borrow 借阅记录
   * @param violationType 违规类型
   * @return ResponseData
   */
  private ResponseData handleViolation(Borrow borrow, ViolationType violationType) {
    int score = 0; // 即将扣除的信誉分
    // 判断违规类型
    switch (violationType) {
      case EXPIRED -> score = 10;
      case OVERDUE -> score = 20;
      case LOST -> score = 40;
    }

    // 生成违规记录
    ViolationRecord violationRecord = new ViolationRecord(borrow.getUserId(), borrow.getId(), violationType);
    int i1 = violationRecordMapper.insert(violationRecord);

    // 相应扣除信用分
    UserCredit userCredit = userCreditMapper.selectOne(
            Wrappers.<UserCredit>lambdaQuery()
                    .eq(UserCredit::getUserId, borrow.getUserId())
    );
    userCredit.setCreditScore(Math.max(userCredit.getCreditScore() - score, 0));
    int i2 = userCreditMapper.updateById(userCredit);

    if (i1 > 0 && i2 > 0) {
      return ResponseData.success();
    }
    throw new SqlFailedException("违规行为处理失败");
  }






  @Override
  public ResponseData handleBorrow(Long id) {
    Borrow borrow = borrowMapper.selectById(id);
    // 检验借阅初始状态
    if(!borrow.getStatus().equals(BorrowStatus.APPLIED)){
      throw new BorrowStatusException("当前借阅状态禁止操作");
    }
    // 修改为借阅中状态
    return setBorrowStatus(id, BorrowStatus.BORROWED);
  }
}
