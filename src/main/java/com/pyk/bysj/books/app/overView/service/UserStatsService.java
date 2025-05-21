package com.pyk.bysj.books.app.overView.service;

import java.util.List;
import java.util.Map;

public interface UserStatsService {
  List<Map<String, Object>> getRecentRegistrations();
  Map<String, Long> getCreditDistribution();
}
