package com.pyk.bysj.books.app.book.controller.admin;

import com.alibaba.fastjson2.JSON;
import com.pyk.bysj.books.app.book.service.admin.BookService;
import com.pyk.bysj.books.exception.BookUploadException;
import com.pyk.bysj.books.model.dto.BookDTO;
import com.pyk.bysj.books.model.dto.EbookDTO;
import com.pyk.bysj.books.model.entity.Book;
import com.pyk.bysj.books.utils.ResponseData;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/admin")
public class BookController {
  private final BookService bookService;

  @Autowired
  public BookController(BookService bookService) {
    this.bookService = bookService;
  }

  @PostMapping("/bookUpload")
  public ResponseData bookUpload(@RequestPart("bookData") @Valid BookDTO bookDTO, @RequestPart("file") MultipartFile file, @RequestPart("cover") MultipartFile cover) throws IOException {
    // 校验封面图片格式
    if (!cover.getContentType().startsWith("image/")) {
      throw new BookUploadException("书籍封面仅支持图片文件", 400);
    }

    String fileName = file.getOriginalFilename();
    // 校验电子书EPUB格式
    if(!fileName.endsWith(".epub")) {
      throw new BookUploadException("电子书仅支持EPUB格式", 400);
    }

    // 临时存储
    Path projectDir = Paths.get(System.getProperty("user.dir"));
    Path contentTempPath = projectDir.resolve("uploads/tmp/fileUploads").resolve(UUID.randomUUID() + ".epub");
    Path CoverTempPath = projectDir.resolve("uploads/tmp/coverUploads").resolve(UUID.randomUUID() + ".jpg");
    file.transferTo(contentTempPath);
    cover.transferTo(CoverTempPath);

    // 调用Python解析
    String result = executePythonParser(bookDTO.getTitle(), contentTempPath.toString(), fileName, CoverTempPath.toString());

    // 添加书籍基本信息
    EbookDTO ebookDTO = JSON.parseObject(result, EbookDTO.class);
    Book book = bookDTO.toEntity();
    book.setCover(ebookDTO.getCover_path());
    book.setEbook(ebookDTO.getOriginal_path());

    return bookService.addBook(book);
  }

  private String executePythonParser(String bookName, String filePath, String fileName, String coverPath) throws UnsupportedEncodingException {
    // 获取python脚本位置
    URL resourceUrl = Objects.requireNonNull(getClass().getResource("/scripts/epub_parser.py"));
    String decodedPath = URLDecoder.decode(resourceUrl.getPath(), StandardCharsets.UTF_8);

    // 处理Windows路径开头的/
    String safePath = decodedPath.startsWith("/") && System.getProperty("os.name").contains("Win")
            ? decodedPath.substring(1)
            : decodedPath;
    System.out.println(safePath);

    // 建立进程调用脚本
    ProcessBuilder pb = new ProcessBuilder(
            "python",
            safePath,
            "--bookName", bookName,
            "--filePath", filePath.replace("\\", "/"),
            "--fileName", fileName,
            "--coverPath", coverPath.replace("\\", "/")
    );

    try {
      // 启动进程
      Process p = pb.start();

      // 读取stdout和stderr
      String output = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))
              .lines().collect(Collectors.joining("\n"));

      String error = new BufferedReader(new InputStreamReader(p.getErrorStream(), StandardCharsets.UTF_8))
              .lines().collect(Collectors.joining("\n"));

      System.out.println("output: " + output);
      System.out.println("error = " + error);

      // 带超时等待
      boolean finished = p.waitFor(10, TimeUnit.MINUTES);
      if (!finished) {
        p.destroy();
        throw new BookUploadException("解析超时", 500);
      }

      // 检查Python是否报错
      if (p.exitValue() != 0) {
        throw new BookUploadException(
                "Python脚本错误: " + (error.isEmpty() ? output : error),
                500
        );
      }

      return output;
    } catch (IOException | InterruptedException e) {
      throw new BookUploadException("执行失败: " + e.getMessage(), 500);
    }
  }





}
