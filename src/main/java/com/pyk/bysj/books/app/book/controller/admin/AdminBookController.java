package com.pyk.bysj.books.app.book.controller.admin;

import com.alibaba.fastjson2.JSON;
import com.pyk.bysj.books.app.book.service.admin.AdminBookService;
import com.pyk.bysj.books.exception.book.BookUploadException;
import com.pyk.bysj.books.model.dto.BookDTO;
import com.pyk.bysj.books.model.dto.BorrowStatusDTO;
import com.pyk.bysj.books.model.dto.EbookDTO;
import com.pyk.bysj.books.model.entity.Book;
import com.pyk.bysj.books.utils.ResponseData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/admin")
public class AdminBookController {
  private final AdminBookService bookService;

  @Autowired
  public AdminBookController(AdminBookService bookService) {
    this.bookService = bookService;
  }

  @PostMapping("/bookUpload")
  public ResponseData bookUpload(
          @RequestPart("bookData") @Valid BookDTO bookDTO,
          @RequestPart("file") MultipartFile file,
          @RequestPart("cover") MultipartFile cover
  ) throws IOException {
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

    return bookService.addBook(book, bookDTO.getBookNumber());
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
        System.err.println("Python脚本错误: " + (error.isEmpty() ? output : error));
        throw new BookUploadException("上传解析出错",500);
      }

      return output;
    } catch (IOException | InterruptedException e) {
      throw new BookUploadException("执行失败: " + e.getMessage(), 500);
    }
  }

  @PostMapping("/updateBook")
  public ResponseData updateBook(
          @RequestParam("bookId")
          @NotNull(message = "id不能为空")
          @Positive(message = "id必须为正整数")
          Integer bookId,
          @RequestPart("bookData")
          @Valid
          BookDTO bookDTO
  ){
    Book book = bookDTO.toEntity();
    book.setId(bookId);
    return bookService.updateBook(book);
  }

  @PostMapping("/deleteBook")
  public ResponseData deleteBook(
          @RequestParam("bookId")
          @NotNull(message = "id不能为空")
          @Positive(message = "id必须为正整数")
          Integer bookId
  ) {
    return bookService.deleteBook(bookId);
  }

  @PostMapping("/setBookNumber")
  public ResponseData setBookNumber(
          @RequestParam("bookId")
          @NotNull(message = "id不能为空")
          @Positive(message = "id必须为正整数")
          Integer bookId,
          @RequestParam("bookNumber")
          @NotNull(message = "书本库存不能为空")
          @PositiveOrZero(message = "书本库存必须为非负整数") Integer bookNumber
  ){
    return bookService.setBookNumber(bookId, bookNumber);
  }


}
