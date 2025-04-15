package com.pyk.bysj.books.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ParseUtil {

  /**
   * 将对象非空字段转为Map（下划线风格字段名）
   * @param object 对象
   * @return 对象中非空字段组合的Map
   * @param <T> 泛型
   * @throws IllegalAccessException 异常
   */
  public static <T> Map<String, Object> toUnderlineMap(T object) throws IllegalAccessException {
    Map<String, Object> map = new HashMap<>();
    if (object == null) {
      return map;
    }

    Class<?> clazz = object.getClass();
    for (Field field : clazz.getDeclaredFields()) {
      field.setAccessible(true);
      Object value = field.get(object);
      if (value != null && !"".equals(value)) {
        // 驼峰转下划线（符合MP的字段命名规则）
        String fieldName = camelToUnderline(field.getName());
        map.put(fieldName, value);
      }
    }
    return map;
  }

  /**
   * 驼峰转下划线
   * @param camel 原驼峰字符串
   * @return 转化后的下划线字符串
   */
  private static String camelToUnderline(String camel) {
    return camel.replaceAll("([A-Z])", "_$1").toLowerCase();
  }

  /**
   * 将StringID转为Long
   * @param value StringId
   * @return LongId
   */
  public static Long StringIdParseLong(String value) {
    if (value == null) return null;
    try {
      long l = Long.parseLong(value.trim());
      if (l < 0) throw new IllegalArgumentException("无效id");
      return l;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("无效id");
    }
  }

  /**
   * 将其他类型数据转为Integer
   * @param value 原始数据
   * @param defaultValue 默认值
   * @return Integer
   */
  public static Integer safeToInt(Object value, Integer defaultValue) {
    if (value == null) return defaultValue;
    try {
      return Integer.valueOf(value.toString().trim());
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  /**
   * 将其他类型数据转为Integer
   * @param value 原始数据
   * @return Integer，失败默认返回null
   */
  public static Integer safeToInt(Object value) {
    if (value == null) return null;
    try {
      return Integer.valueOf(value.toString().trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }


}
