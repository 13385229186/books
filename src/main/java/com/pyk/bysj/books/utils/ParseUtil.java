package com.pyk.bysj.books.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ParseUtil {

  /**
   * 将对象非空字段转为Map
   * @param object 对象
   * @return 对象中非空字段组合的Map
   * @param <T> 泛型
   * @throws IllegalAccessException 异常
   */
  public static <T> Map<String, Object> toMap(T object) throws IllegalAccessException {
    Map<String, Object> map = new HashMap<>();
    if (object == null) {
      return map;
    }

    Class<?> clazz = object.getClass();
    for (Field field : clazz.getDeclaredFields()) {
      field.setAccessible(true);
      Object value = field.get(object);
      if (value != null && !"".equals(value)) {
        map.put(field.getName(), value);
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
   * 将StringId转为LongId，id必须为正整数
   * @param value StringId
   * @return LongId
   */
  public static Long StringIdParseLong(String value) {
    if (value == null) return null;
    try {
      long l = Long.parseLong(value.trim());
      if (l <= 0) throw new IllegalArgumentException("id必须为正整数");
      return l;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("无效id");
    }
  }

  /**
   * 将StringID转为IntegerId，id必须为正整数
   * @param value StringId
   * @return IntegerId
   */
  public static Integer StringIdParseInteger(Object value) {
    if (value == null) return null;
    try {
      int i = Integer.parseInt(value.toString().trim());
      if (i <= 0) throw new IllegalArgumentException("id必须为正整数");
      return i;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("无效id");
    }
  }

  /**
   * 将StringThing转为IntegerThing，且必须为正整数
   * @param value StringThing
   * @param thing 主语，如id、书本库存等
   * @return IntegerThing
   */
  public static Integer StringIdParseInteger(Object value, String thing) {
    if (value == null) return null;
    try {
      int i = Integer.parseInt(value.toString().trim());
      if (i <= 0) throw new IllegalArgumentException(thing + "必须为正整数");
      return i;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("无效" + thing);
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
   * 将对象中所有空字符串或纯空格的字段设置为null
   * @param target 要处理的对象
   */
  public static void convertEmptyStringsToNull(Object target) {
    if (target == null) {
      return;
    }

    Class<?> clazz = target.getClass();
    // 遍历所有字段(包括父类字段)
    ReflectionUtils.doWithFields(clazz, field -> {
      // 设置可访问私有字段
      ReflectionUtils.makeAccessible(field);

      // 获取字段值
      Object value = field.get(target);

      // 只处理String类型字段
      if (value instanceof String strValue) {
        if (strValue.trim().isEmpty()) {
          field.set(target, null); // 将空字符串设为null
        }
      }
    });
  }

  /**
   * 多格式尝试解析日期时间
   * @param dateTimeStr 日期时间字符串
   * @return LocalDateTime或抛出解析失败异常
   */
  public static LocalDateTime parseFlexibleDateTime(String dateTimeStr) {
    if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
      return null;
    }

    System.out.println("dateTimeStr::::::::::::::"+dateTimeStr);

    String trimmed = dateTimeStr.trim();

    // 定义支持的LocalDate格式
    List<DateTimeFormatter> dateFormatters = Arrays.asList(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd")
    );

    // 尝试每种LocalDate格式
    for (DateTimeFormatter formatter : dateFormatters) {
      try {
        LocalDate date = LocalDate.parse(trimmed, formatter);
        return date.atStartOfDay();
      } catch (DateTimeParseException e) {
        // 忽略异常，继续尝试下一个格式
        log.info(e.getMessage());
      }
    }

    // 定义支持的LocalDateTime格式
    List<DateTimeFormatter> dateTimeFormatters = Arrays.asList(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,      // "yyyy-MM-dd'T'HH:mm:ss"
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss"),
            DateTimeFormatter.ISO_OFFSET_DATE_TIME     // 带时区
    );

    // 尝试每种LocalDateTime格式
    for (DateTimeFormatter formatter : dateTimeFormatters) {
      try {
        if (formatter == DateTimeFormatter.ISO_OFFSET_DATE_TIME) {
          return LocalDateTime.ofInstant(Instant.parse(trimmed), ZoneId.systemDefault());
        }
        return LocalDateTime.parse(trimmed, formatter);
      } catch (DateTimeParseException e) {
        // 忽略异常，继续尝试下一个格式
        log.info(e.getMessage());
      }
    }

    // 尝试解析时间戳
    try {
      long timestamp = Long.parseLong(trimmed);
      return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    } catch (NumberFormatException e) {
      throw new DateTimeParseException("无法解析日期时间，所有支持的格式都尝试失败: " + trimmed, trimmed, 0);
    }
  }


}
