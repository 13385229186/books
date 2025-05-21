package com.pyk.bysj.books.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseData {
  private int code;
  private String msg;
  private String status;
  private Object data;

  public static ResponseData success() {
    return new ResponseData(200, "success", "success", null);
  }

  public static ResponseData success(Object data){
    return new ResponseData(200, "success", "success", data);
  }
  public static ResponseData fail() {
    return new ResponseData(500, "fail", "fail", null);
  }

  public static ResponseData fail(String msg){
    return new ResponseData(500, msg, "fail", null);
  }

  public static ResponseData fail(int code, String msg){
    return new ResponseData(code, msg, "fail", null);
  }

  public static ResponseData fail(int code, String msg, Object data){
    return new ResponseData(code, msg, "fail", data);
  }

}
