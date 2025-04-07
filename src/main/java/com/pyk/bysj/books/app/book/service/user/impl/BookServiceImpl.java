package com.pyk.bysj.books.app.book.service.user.impl;

import com.pyk.bysj.books.app.book.service.user.BookService;
import com.pyk.bysj.books.model.dto.BookDTO;
import com.pyk.bysj.books.model.entity.Book;
import org.springframework.stereotype.Service;

import java.util.List;

public class BookServiceImpl implements BookService {
  @Override
  public List<Book> bookList(BookDTO bookDTO) {
    return List.of();
  }

  @Override
  public Book getBookById(Integer id) {
    return null;
  }
}
