package com.pyk.bysj.books.utils;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class WebUtil {
  /**
   * 将JSON字符串写入HTTP响应
   * @param response HttpServletResponse对象
   * @param jsonContent 要输出的JSON字符串
   */
  public static void renderJson(HttpServletResponse response, String jsonContent){
    response.setStatus(HttpServletResponse.SC_OK);   // 200响应码
    response.setContentType("application/json;charset=UTF-8");

    try (PrintWriter writer = response.getWriter()) {
      writer.print(jsonContent);
      writer.flush();
    } catch (IOException e) {
//      log.error("响应写入失败", e);
//      throw new BusinessException("系统繁忙");
      e.printStackTrace();
    }
  }
}
