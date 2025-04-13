package com.pyk.bysj.books.app.book.service.admin.impl;

import com.pyk.bysj.books.app.book.service.admin.AdminBookService;
import com.pyk.bysj.books.exception.general.NotExistException;
import com.pyk.bysj.books.exception.general.OperationFailedException;
import com.pyk.bysj.books.exception.general.SqlFailedException;
import com.pyk.bysj.books.mapper.BookMapper;
import com.pyk.bysj.books.mapper.BookNumberMapper;
import com.pyk.bysj.books.model.entity.Book;
import com.pyk.bysj.books.model.entity.BookNumber;
import com.pyk.bysj.books.utils.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AdminBookServiceImpl implements AdminBookService {
  @Autowired
  private BookMapper bookMapper;
  @Autowired
  private BookNumberMapper bookNumberMapper;

  @Override
  public ResponseData addBook(Book book, Integer bookNumber) {
    // 根据isbn检查书籍是否已添加
    List<Book> isbnList = bookMapper.selectByMap(Map.of("isbn", book.getIsbn()));
    if(!isbnList.isEmpty()){
      throw new OperationFailedException("请勿重复添加书籍：" + isbnList.get(0).getTitle());
    }

    // 若为新书，添加该书信息
    int insert1 = bookMapper.insert(book);
    if (insert1 > 0) {
      int insert2 = bookNumberMapper.insert(new BookNumber(book.getId(), bookNumber));
      if (insert2 > 0) {
        return ResponseData.success();
      }
    }
    throw new SqlFailedException("书籍信息添加失败");
  }

  @Override
  public ResponseData updateBook(Book book) {
    int i = bookMapper.updateById(book);
    if (i > 0) {
      return ResponseData.success();
    }
    throw new SqlFailedException("更新失败");
  }

  @Override
  public ResponseData deleteBook(Integer id) {
    int i = bookMapper.deleteById(id);
    if (i > 0) {
      return ResponseData.success();
    }
    throw new SqlFailedException("删除失败");
  }

  @Override
  public ResponseData setBookNumber(Integer id, Integer number) {
    List<BookNumber> bookNumbers = bookNumberMapper.selectByMap(Map.of("book_id", id));
    if(bookNumbers.isEmpty()){
      throw new NotExistException("书籍不存在");
    }
    bookNumbers.get(0).setNumber(number);
    boolean b = bookNumberMapper.insertOrUpdate(bookNumbers.get(0));
    if(b){
      return ResponseData.success();
    }else{
      throw new SqlFailedException("设置失败");
    }
  }
}
