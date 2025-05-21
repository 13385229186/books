package com.pyk.bysj.books.app.overView.service;

import com.pyk.bysj.books.model.dto.overView.BookOverviewVO;

import java.util.Map;

public interface BookStatsService {
  Map<String, Object> getBookOverview();
}
