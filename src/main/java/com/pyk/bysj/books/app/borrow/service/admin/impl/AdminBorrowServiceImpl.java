package com.pyk.bysj.books.app.borrow.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.pyk.bysj.books.app.book.service.admin.AdminBookService;
import com.pyk.bysj.books.app.book.service.user.UserBookService;
import com.pyk.bysj.books.app.borrow.service.admin.AdminBorrowService;
import com.pyk.bysj.books.enums.BorrowStatus;
import com.pyk.bysj.books.enums.Role;
import com.pyk.bysj.books.enums.ViolationType;
import com.pyk.bysj.books.exception.borrow.BorrowStatusException;
import com.pyk.bysj.books.exception.general.OperationFailedException;
import com.pyk.bysj.books.exception.general.SqlFailedException;
import com.pyk.bysj.books.mapper.BorrowMapper;
import com.pyk.bysj.books.mapper.UserCreditMapper;
import com.pyk.bysj.books.mapper.ViolationRecordMapper;
import com.pyk.bysj.books.model.dto.*;
import com.pyk.bysj.books.model.entity.*;
import com.pyk.bysj.books.utils.ParseUtil;
import com.pyk.bysj.books.utils.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Wrapper;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional
public class AdminBorrowServiceImpl extends ServiceImpl<BorrowMapper, Borrow> implements AdminBorrowService {
  private final BorrowMapper borrowMapper;
  private final ViolationRecordMapper violationRecordMapper;
  private final UserCreditMapper userCreditMapper;
  private final UserBookService userBookService;
  private final AdminBookService adminBookService;

  @Value("${app.borrow-expiration-time}")
  private Integer borrowExpirationTime;

  @Autowired
  public AdminBorrowServiceImpl(final BorrowMapper borrowMapper, ViolationRecordMapper violationRecordMapper, UserCreditMapper userCreditMapper, UserBookService userBookService, AdminBookService adminBookService) {
    this.borrowMapper = borrowMapper;
    this.violationRecordMapper = violationRecordMapper;
    this.userCreditMapper = userCreditMapper;
    this.userBookService = userBookService;
    this.adminBookService = adminBookService;
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
        // 释放库存
        setBookNumber(borrow.getBookId(), 1, true);
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
    ViolationRecord violationRecord = new ViolationRecord(borrow.getUserId(), borrow.getBookId(), borrow.getId(), violationType);
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
        // 释放库存
        return setBookNumber(borrow.getBookId(), 1, true);
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
        // 若按时归还，则加5分信用分
        if(borrow.getStatus() == BorrowStatus.BORROWED){
          setCreditScore(borrow.getUserId(), 5, true);
        }
        // 释放库存
        return setBookNumber(borrow.getBookId(), 1, true);
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

  /**
   * 实体书本数量变化
   * @param bookId 书籍id
   * @param gapNumber 书本变化量
   * @param isPlus true为加，false为减
   * @return ResponseData
   */
  private ResponseData setBookNumber(Integer bookId, Integer gapNumber, Boolean isPlus){
    int bookNumber = userBookService.getBookNumberById(bookId);
    return adminBookService.setBookNumber(
            bookId,
            isPlus ? bookNumber + gapNumber : bookNumber - gapNumber);
  }

  @Override
  public ResponseData handleBorrow(Long id) {
    return setBorrowStatus(id, BorrowStatus.BORROWED);
  }

  @Override
  public ListQueryResult<BorrowDTO> borrowList(Map<String, Object> borrowMap, PageParam pageParam) {
    // 开启分页查询
    Page<BorrowDTO> page = new Page<>(pageParam.getCurrent(), pageParam.getPageSize());
    // 筛选条件Wrapper
    MPJLambdaWrapper<Borrow> wrapper = JoinWrappers.lambda(Borrow.class);
    wrapper.selectAll()
            .select(User::getName)
            .select(User::getPhone)
            .innerJoin(User.class, User::getId, Borrow::getUserId)
            .select(Book::getTitle)
            .innerJoin(Book.class, Book::getId, Borrow::getBookId);

    borrowMap.forEach((key, value) -> {
      if (StringUtils.isNotBlank(key) && value != null) {
        // 根据参数名动态添加条件
        switch (key) {
          // 精确匹配字段
          case "id":
            wrapper.eq(Borrow::getId, ParseUtil.StringIdParseLong(value.toString()));
            break;
          case "userId":
            wrapper.eq(Borrow::getUserId, Integer.parseInt(value.toString()));
            break;
          case "bookId":
            wrapper.eq(Borrow::getBookId, Integer.parseInt(value.toString()));
            break;
          case "borrowDays":
            wrapper.eq(Borrow::getBorrowDays, Integer.parseInt(value.toString()));
            break;
          case "status":
            BorrowStatus borrowStatus = BorrowStatus.fromValue(value.toString());
            wrapper.eq(Borrow::getStatus, borrowStatus);
            // 若筛选已申请或已过期状态，需刷新所有已申请但实际过期的借阅
            if(borrowStatus == BorrowStatus.APPLIED || borrowStatus == BorrowStatus.EXPIRED){
              batchProcessExpire();
            }
            break;
          case "name":
            wrapper.eq(User::getName, value.toString());
            break;
          case "phone":
            wrapper.eq(User::getPhone, value.toString());
            break;

          //范围匹配字段
          case "borrowTime":
            LocalDateTime borrowTime = ParseUtil.parseFlexibleDateTime(value.toString());
            // 匹配当天的记录
            wrapper.between(Borrow::getBorrowTime,
                    borrowTime.truncatedTo(ChronoUnit.DAYS),
                    borrowTime.truncatedTo(ChronoUnit.DAYS).plusDays(1));
            break;
          case "dueTime":
            LocalDateTime dueTime = ParseUtil.parseFlexibleDateTime(value.toString());
            // 匹配当天的记录
            wrapper.between(Borrow::getDueTime,
                    dueTime.truncatedTo(ChronoUnit.DAYS),
                    dueTime.truncatedTo(ChronoUnit.DAYS).plusDays(1));
            break;
          case "returnTime":
            LocalDateTime returnTime = ParseUtil.parseFlexibleDateTime(value.toString());
            // 匹配当天的记录
            wrapper.between(Borrow::getReturnTime,
                    returnTime.truncatedTo(ChronoUnit.DAYS),
                    returnTime.truncatedTo(ChronoUnit.DAYS).plusDays(1));
            break;
          case "createTime":
            LocalDateTime createTime = ParseUtil.parseFlexibleDateTime(value.toString());
            // 匹配当天的记录
            wrapper.between(Borrow::getCreatedAt,
                    createTime.truncatedTo(ChronoUnit.DAYS),
                    createTime.truncatedTo(ChronoUnit.DAYS).plusDays(1));
            break;

          // 模糊搜索字段
          case "title":
            wrapper.like(Book::getTitle, "%" + value + "%");
            break;

          // 默认处理（防止恶意参数）
          default:
            log.warn("未知查询参数: {}={}", key, value);
            throw new IllegalArgumentException("参数处理失败: " + key);
        }
      }
    });

    Page<BorrowDTO> result = borrowMapper.selectJoinPage(page, BorrowDTO.class, wrapper);

    // 获取分页数据
    List<BorrowDTO> records = result.getRecords(); // 当前页数据

    // 实时同步已过期借阅
    List<Long> expiredIds = batchProcessExpire(records);
    if (!expiredIds.isEmpty()) {
      records.forEach(record -> {
        if(expiredIds.contains(record.getId())){
          record.setStatus(BorrowStatus.EXPIRED);
        }
      });
    }

    return new ListQueryResult<>(
            records,
            true,
            (int)result.getTotal(),
            (int)result.getPages(),
            (int)result.getCurrent());
  }

  /**
   * 检查是否存在未处理的过期借阅
   */
  private void batchProcessExpire() {
    // 查询所有已申请且实际过期的借阅
    List<Borrow> borrows = borrowMapper.selectList(
            new LambdaQueryWrapper<Borrow>()
                    .eq(Borrow::getStatus, BorrowStatus.APPLIED)
                    .lt(borrow -> borrow.getCreatedAt().plusSeconds(borrowExpirationTime), LocalDateTime.now())
    );

    if (borrows.isEmpty()) return ;

    // 处理过期违规业务逻辑
    borrows.forEach(borrow -> {
      handleViolationStatus(borrow, ViolationType.EXPIRED);
      borrow.setStatus(BorrowStatus.EXPIRED);
    });

    // 批量更新状态
    if(!updateBatchById(borrows)){
      throw new SqlFailedException("状态更新失败");
    }
    log.info("所有过期借阅已刷新");
  }

  /**
   * 检查指定记录中是否存在未处理的过期借阅
   * @param borrowDTOs 指定记录范围
   */
  private List<Long> batchProcessExpire(List<BorrowDTO> borrowDTOs) {
    List<Borrow> borrows = borrowDTOs.stream()
            .filter(record -> record.getStatus() == BorrowStatus.APPLIED && LocalDateTime.now().isAfter(record.getCreatedAt().plusSeconds(borrowExpirationTime)))
            .map(BorrowDTO::toBorrow)
            .toList();

    if (borrows.isEmpty()) return Collections.emptyList();

    // 处理过期违规业务逻辑
    borrows.forEach(borrow -> {
      handleViolationStatus(borrow, ViolationType.EXPIRED);
      borrow.setStatus(BorrowStatus.EXPIRED);
    });

    // 批量更新状态
    if(!updateBatchById(borrows)){
      throw new SqlFailedException("状态更新失败");
    }
    return borrows.stream().map(Borrow::getId).collect(Collectors.toList());
  }

  @Override
  public ListQueryResult<ViolationDTO> violationList(Map<String, Object> violationMap, PageParam pageParam) {
    // 开启分页查询
    Page<ViolationDTO> page = new Page<>(pageParam.getCurrent(), pageParam.getPageSize());
    // 筛选条件Wrapper
    MPJLambdaWrapper<ViolationRecord> wrapper = JoinWrappers.lambda(ViolationRecord.class);
    wrapper.selectAll()
            .select(User::getName)
            .select(User::getPhone)
            .innerJoin(User.class, User::getId, ViolationRecord::getUserId)
            .select(Book::getTitle)
            .innerJoin(Book.class, Book::getId, ViolationRecord::getBookId);

    violationMap.forEach((key, value) -> {
      if (StringUtils.isNotBlank(key) && value != null) {
        // 根据参数名动态添加条件
        switch (key) {
          // 精确匹配字段
          case "id":
            wrapper.eq(ViolationRecord::getId, ParseUtil.StringIdParseLong(value.toString()));
            break;
          case "userId":
            wrapper.eq(ViolationRecord::getUserId, Integer.parseInt(value.toString()));
            break;
          case "bookId":
            wrapper.eq(ViolationRecord::getBookId, Integer.parseInt(value.toString()));
            break;
          case "borrowId":
            wrapper.eq(ViolationRecord::getBorrowId, ParseUtil.StringIdParseLong(value.toString()));
            break;
          case "violationType":
            wrapper.eq(ViolationRecord::getViolationType, ViolationType.fromValue(value.toString()));
            break;
          case "name":
            wrapper.eq(User::getName, value.toString());
            break;
          case "phone":
            wrapper.eq(User::getPhone, value.toString());
            break;

          // 模糊搜索字段
          case "title":
            wrapper.like(Book::getTitle, "%" + value + "%");
            break;

          // 默认处理（防止恶意参数）
          default:
            log.warn("未知查询参数: {}={}", key, value);
            throw new IllegalArgumentException("参数处理失败: " + key);
        }
      }
    });

    Page<ViolationDTO> result = violationRecordMapper.selectJoinPage(page, ViolationDTO.class, wrapper);

    // 获取分页数据
    List<ViolationDTO> records = result.getRecords(); // 当前页数据

    return new ListQueryResult<>(
            records,
            true,
            (int)result.getTotal(),
            (int)result.getPages(),
            (int)result.getCurrent());
  }

}
