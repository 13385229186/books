package com.pyk.bysj.books.mapper;

import com.pyk.bysj.books.model.entity.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class BookMapperTest {
  @Autowired private BookMapper bookMapper;
  @Test
  public void test() {
    List<Book> books = bookMapper.selectList(null);
    books.forEach(System.out::println);
  }
}
