package com.pyk.bysj.books.app.book.service.admin.impl;

import com.pyk.bysj.books.app.book.service.admin.BookService;
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
public class BookServiceImpl implements BookService {
  @Autowired
  private BookMapper bookMapper;
  @Autowired
  private BookNumberMapper bookNumberMapper;

  @Override
  @Transactional
  public ResponseData addBook(Book book, Integer bookNumber) {
    // 根据isbn检查书籍是否已添加
    List<Book> isbnList = bookMapper.selectByMap(Map.of("isbn", book.getIsbn()));
    if(!isbnList.isEmpty()){
      return ResponseData.fail("请勿重复添加书籍：" + isbnList.get(0).getTitle());
    }

    // 若为新书，添加该书信息
    int insert1 = bookMapper.insert(book);
    int insert2 = bookNumberMapper.insert(new BookNumber(book.getIsbn(), bookNumber));
    if (insert1 > 0 && insert2 > 0) {
      return ResponseData.success();
    }
    return ResponseData.fail("书籍信息添加失败！");
  }

  @Override
  public ResponseData updateBook(Book book) {
    return null;
  }

  @Override
  public ResponseData deleteBook(Integer id) {
    return null;
  }

  @Override
  public ResponseData setBookNumber(String isbn, Integer number) {
    List<BookNumber> bookNumbers = bookNumberMapper.selectByMap(Map.of("isbn", isbn));
    if(bookNumbers.isEmpty()){
      return ResponseData.fail("书籍不存在");
    }
    bookNumbers.get(0).setNumber(number);
    boolean b = bookNumberMapper.insertOrUpdate(bookNumbers.get(0));
    return b ? ResponseData.success() : ResponseData.fail(500, "设置失败");
  }
}
