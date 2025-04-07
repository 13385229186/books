package com.pyk.bysj.books.app.book.service.admin.impl;

import com.pyk.bysj.books.app.book.service.admin.BookService;
import com.pyk.bysj.books.mapper.BookMapper;
import com.pyk.bysj.books.model.entity.Book;
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

  @Override
  @Transactional
  public ResponseData addBook(Book book) {
    // 根据isbn检查书籍是否已添加
    List<Book> isbnList = bookMapper.selectByMap(Map.of("isbn", book.getIsbn()));
    if(!isbnList.isEmpty()){
      return ResponseData.fail("请勿重复添加书籍：" + isbnList.get(0).getTitle());
    }

    // 若为新书，添加该书信息
    int insert = bookMapper.insert(book);
    if (insert > 0) {
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
}
