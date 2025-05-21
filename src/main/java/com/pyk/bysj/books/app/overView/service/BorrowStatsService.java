package com.pyk.bysj.books.app.overView.service;

import java.util.List;
import java.util.Map;

public interface BorrowStatsService {
  List<Map<String, Object>> getBorrowCountByCategory();
}
