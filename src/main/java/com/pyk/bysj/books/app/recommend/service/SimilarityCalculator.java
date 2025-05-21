package com.pyk.bysj.books.app.recommend.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pyk.bysj.books.mapper.BookSimilarityMapper;
import com.pyk.bysj.books.mapper.UserBehaviorMapper;
import com.pyk.bysj.books.model.entity.BookSimilarity;
import com.pyk.bysj.books.model.entity.UserBehavior;
import com.pyk.bysj.books.utils.PythonScriptExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimilarityCalculator {
  private final UserBehaviorMapper userBehaviorMapper;
  private final BookSimilarityMapper bookSimilarityMapper;

  @Scheduled(cron = "0 0 3 * * ?") // 每天凌晨3点执行
  public void calculateAndStoreSimilarities() throws IOException {
    // 从数据库获取全量行为数据
    List<UserBehavior> behaviors = userBehaviorMapper.selectList(new QueryWrapper<UserBehavior>());

    // 准备Python输入
    List<Map<String, Object>> behaviorData = behaviors.stream()
            .map(b -> {
              Map<String, Object> item = new HashMap<>();
              item.put("user_id", b.getUserId());
              item.put("book_id", b.getBookId());
              item.put("weight", b.getWeight());
              return item;
            })
            .collect(Collectors.toList());

//    String behaviorDataJson = JSON.toJSONString(behaviorData);
//    System.out.println("behaviorDataJson = " + behaviorDataJson);
//    Map<String, String> args = new HashMap<>();
//    args.put("behavior_data", behaviorDataJson);

    ObjectMapper mapper = new ObjectMapper();
    Map<String, String> args = new HashMap<>();
    args.put("behavior_data", mapper.writeValueAsString(behaviorData));

    // 调用Python脚本
    String result = PythonScriptExecutor.executePythonScript(
            "book_similarity_calculator.py", args, 30);

    System.out.println("result = " + result);

    // 解析Python返回的结果
    Map<Integer, Map<Integer, Float>> similarityMap = JSON.parseObject(result, new TypeReference<>() {});

    // 批量存储到数据库
    saveSimilarities(similarityMap);
  }

  private void saveSimilarities(Map<Integer, Map<Integer, Float>> similarityMap) {
    // 先清空旧数据
    bookSimilarityMapper.delete(null);

    // 准备批量插入数据
    List<BookSimilarity> entities = new ArrayList<>();

    similarityMap.forEach((sourceBookId, targetMap) -> {
      targetMap.forEach((targetBookId, score) -> {
        entities.add(new BookSimilarity(sourceBookId, targetBookId, score));
      });
    });

    // 批量插入
    bookSimilarityMapper.insert(entities);
    log.info("成功存储 {} 条书籍相似度记录", entities.size());
  }
}