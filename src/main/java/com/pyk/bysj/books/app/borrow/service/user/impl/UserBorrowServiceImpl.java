package com.pyk.bysj.books.app.borrow.service.user.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pyk.bysj.books.app.book.service.admin.AdminBookService;
import com.pyk.bysj.books.app.book.service.user.UserBookService;
import com.pyk.bysj.books.app.borrow.service.admin.AdminBorrowService;
import com.pyk.bysj.books.app.borrow.service.user.UserBorrowService;
import com.pyk.bysj.books.enums.BookStatus;
import com.pyk.bysj.books.enums.BorrowStatus;
import com.pyk.bysj.books.exception.borrow.BorrowStatusException;
import com.pyk.bysj.books.exception.general.OperationFailedException;
import com.pyk.bysj.books.exception.general.SqlFailedException;
import com.pyk.bysj.books.exception.borrow.BookNotAvailableException;
import com.pyk.bysj.books.exception.borrow.CreditException;
import com.pyk.bysj.books.mapper.BookMapper;
import com.pyk.bysj.books.mapper.BorrowMapper;
import com.pyk.bysj.books.mapper.UserCreditMapper;
import com.pyk.bysj.books.model.dto.BorrowDTO;
import com.pyk.bysj.books.model.dto.ListQueryResult;
import com.pyk.bysj.books.model.dto.PageParam;
import com.pyk.bysj.books.model.entity.Book;
import com.pyk.bysj.books.model.entity.Borrow;
import com.pyk.bysj.books.model.entity.User;
import com.pyk.bysj.books.model.entity.UserCredit;
import com.pyk.bysj.books.utils.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class UserBorrowServiceImpl implements UserBorrowService {
  private final BookMapper bookMapper;
  private final UserBookService userBookService;
  private final AdminBookService adminBookService;
  private final BorrowMapper borrowMapper;
  private final UserCreditMapper userCreditMapper;
  private final AdminBorrowService adminBorrowService;

  @Autowired
  public UserBorrowServiceImpl(BookMapper bookMapper, UserBookService userBookService, AdminBookService adminBookService, BorrowMapper borrowMapper, UserCreditMapper userCreditMapper, AdminBorrowService adminBorrowService) {
    this.bookMapper = bookMapper;
    this.userBookService = userBookService;
    this.adminBookService = adminBookService;
    this.borrowMapper = borrowMapper;
    this.userCreditMapper = userCreditMapper;
    this.adminBorrowService = adminBorrowService;
  }

  @Override
  public ResponseData borrowBook(User user, Integer bookId, Integer borrowDays) {
    // 查询用户信用分及借书情况
    UserCredit userCredit = userCreditMapper.selectOne(
            Wrappers.<UserCredit>lambdaQuery()
                    .eq(UserCredit::getUserId, user.getId())
    );
    if (userCredit == null) {
      throw new CreditException(500, "用户信用分异常");
    }
    Integer creditScore = userCredit.getCreditScore();
    if (creditScore < 50){
      throw new CreditException("信用分过低，请联系管理员");
    }

    Long userBorrowNumber = borrowMapper.selectCount(
            Wrappers.<Borrow>lambdaQuery()
                    .eq(Borrow::getUserId, user.getId())
                    .in(Borrow::getStatus, BorrowStatus.APPLIED, BorrowStatus.BORROWED, BorrowStatus.OVERDUE)
    );
    if(creditScore >= 80 && userBorrowNumber >= 3) {
      throw new CreditException("当前最多只能同时借阅3本书，请保持良好借书习惯");
    }else if (creditScore < 80 && creditScore > 50 && userBorrowNumber >= 1) {
      throw new CreditException("当前最多只能同时借阅1本书，请保持良好借书习惯");
    }

    // 查询书籍是否存在及状态AVAILABLE
    Book book = bookMapper.selectById(bookId);
    if (book == null || book.getStatus() != BookStatus.AVAILABLE) {
      throw new BookNotAvailableException("实体书尚不可借阅");
    }

    // 生成借阅记录
    Borrow borrow = new Borrow(user.getId(), bookId, borrowDays);
    int insert = borrowMapper.insert(borrow);
    if (insert <= 0) {
      throw new SqlFailedException("申请借阅失败");
    }

    // 修改实体书籍数量
    int bookNumber = userBookService.getBookNumberById(bookId);
    ResponseData responseData2 = adminBookService.setBookNumber(bookId, bookNumber - 1);
    if(responseData2.getCode() != 200){
      return responseData2;
    }

    // 若为最后一本书，修改书籍状态为已借完
    if(bookNumber <= 1){
      book.setStatus(BookStatus.BORROWED);
      ResponseData responseData1 = adminBookService.updateBook(book);
      if(responseData1.getCode() != 200){
        return responseData1;
      }
    }

    return ResponseData.success(borrow);
  }

  @Override
  public ResponseData cancelBorrowBook(User user, Long borrowId) {
    Borrow borrow = borrowMapper.selectById(borrowId);
    // 检验借阅记录是否属于该用户
    if(!Objects.equals(borrow.getUserId(), user.getId())){
      throw new OperationFailedException(400, "用户不匹配，禁止操作");
    }
    // 检验借阅状态是否为“已申请”
    if(!borrow.getStatus().equals(BorrowStatus.APPLIED) && !borrow.getStatus().equals(BorrowStatus.CANCELLED)){
      throw new BorrowStatusException("当前借阅状态禁止操作");
    }
    return adminBorrowService.setBorrowStatus(borrowId, BorrowStatus.CANCELLED);
  }

  @Override
  public ListQueryResult<BorrowDTO> borrowListByUserId(Integer userId, PageParam pageParam) {
    return adminBorrowService.borrowList(Map.of("userId", userId), pageParam);
  }
}
