package com.pyk.bysj.books.app.book.controller.admin;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
public class BookController {

  @PostMapping("/book/upload")
  public String handleUpload(@RequestParam("file") MultipartFile file) throws IOException {
    // 验证EPUB格式
    if(!file.getOriginalFilename().endsWith(".epub")) {
      throw new IllegalArgumentException("仅支持EPUB格式");
    }

    // 生成唯一ID并临时存储
    String bookId = UUID.randomUUID().toString();
    Path tempPath = Paths.get("/tmp/uploads", bookId + ".epub");
    file.transferTo(tempPath);

    // 调用Python解析
    return executePythonParser(bookId, tempPath.toString());
  }
}
