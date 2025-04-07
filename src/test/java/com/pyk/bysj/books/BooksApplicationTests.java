package com.pyk.bysj.books;

import com.pyk.bysj.books.mapper.LoginMapper;
import com.pyk.bysj.books.model.entity.Login;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;

@SpringBootTest
class BooksApplicationTests {


  @Autowired
  private LoginMapper loginMapper;
  @Autowired private PasswordEncoder passwordEncoder;

  @Test void contextLoads() {

    //导入一个用户
    Login sysUser = new Login();

    sysUser.setId(6);
    sysUser.setUserId(5);
    sysUser.setUsername("zhangsan");
    sysUser.setPassword(passwordEncoder.encode("123456"));
    loginMapper.insert(sysUser);

  }

  @Test
  void pythonTest() throws IOException, InterruptedException {
    URL resourceUrl = Objects.requireNonNull(getClass().getResource("/scripts/hello.py"));
    String decodedPath = URLDecoder.decode(resourceUrl.getPath(), StandardCharsets.UTF_8);

    // 处理Windows路径开头的/
    String safePath = decodedPath.startsWith("/") && System.getProperty("os.name").contains("Win")
            ? decodedPath.substring(1)
            : decodedPath;
    System.out.println(safePath);

    ProcessBuilder pb = new ProcessBuilder(
            "python",
            safePath
    );
    Process p = pb.start();

//    // 同时读取stdout和stderr
//    String output = new BufferedReader(new InputStreamReader(p.getInputStream()))
//            .lines().collect(Collectors.joining("\n"));
//
//    String error = new BufferedReader(new InputStreamReader(p.getErrorStream()))
//            .lines().collect(Collectors.joining("\n"));

    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

    String line;

    // 创建了BufferedReader，可以使用BufferedReader的readLine()或read()方法从进程的输入流中读取字符。
    while ((line = reader.readLine()) != null) {
      // 读取Python脚本的输出，并处理返回的数据
      System.out.println("Python script output: " + line);
      // ... 在此处进行进一步的处理
    }

    int exitCode = p.waitFor();// 等待脚本执行结束后获取返回的状态码
    System.out.println("Python script executed with exit code " + exitCode);


  }

}
