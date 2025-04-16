package com.pyk.bysj.books.utils;

import org.springframework.util.ReflectionUtils;

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


}
