package com.pyk.bysj.books;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.pyk.bysj.books.mapper")
@EnableTransactionManagement
public class BooksApplication {

  public static void main(String[] args) {
    SpringApplication.run(BooksApplication.class, args);
  }

}
