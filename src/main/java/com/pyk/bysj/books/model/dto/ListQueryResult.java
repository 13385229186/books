package com.pyk.bysj.books.model.dto;

import com.pyk.bysj.books.model.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListQueryResult<T> {
  /**
   * 查询数据
   */
  List<T> data;
  /**
   * 是否查询成功
   */
  Boolean success = Boolean.TRUE;
  /**
   * 数据总条数
   */
  Integer total;
  /**
   * 数据总页数
   */
  Integer pages;
  /**
   * 当前页
   */
  Integer current;
}
