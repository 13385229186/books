package com.pyk.bysj.books.app.recommend.service;

import com.pyk.bysj.books.model.dto.BookWithHeatScoreDTO;
import com.pyk.bysj.books.model.dto.ListQueryResult;
import com.pyk.bysj.books.model.dto.PageParam;

public interface HotBookService {
  ListQueryResult<BookWithHeatScoreDTO> hotBookList(PageParam pageParam);


}
