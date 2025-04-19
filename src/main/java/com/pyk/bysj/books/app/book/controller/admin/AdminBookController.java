package com.pyk.bysj.books.app.book.controller.admin;

import com.alibaba.fastjson2.JSON;
import com.pyk.bysj.books.app.book.service.admin.AdminBookService;
import com.pyk.bysj.books.config.UploadTmpConfig;
import com.pyk.bysj.books.exception.book.BookUploadException;
import com.pyk.bysj.books.model.dto.BookDTO;
import com.pyk.bysj.books.model.dto.BorrowStatusDTO;
import com.pyk.bysj.books.model.dto.EbookDTO;
import com.pyk.bysj.books.model.entity.Book;
import com.pyk.bysj.books.utils.ParseUtil;
import com.pyk.bysj.books.utils.PythonScriptExecutor;
import com.pyk.bysj.books.utils.ResponseData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin")
public class AdminBookController {
  private final UploadTmpConfig uploadTmpConfig;
  private final AdminBookService bookService;

  @Autowired
  public AdminBookController(UploadTmpConfig uploadTmpConfig, AdminBookService bookService) {
    this.uploadTmpConfig = uploadTmpConfig;
    this.bookService = bookService;
  }

  @PostMapping("/bookUpload")
  public ResponseData bookUpload(
          @RequestPart("bookData") @Valid BookDTO bookDTO,
          @RequestPart(value = "file", required = false) MultipartFile file,
          @RequestPart(value = "cover", required = false) MultipartFile cover
  ){
    Book book = bookDTO.toEntity();

    // 空文件检查
    boolean hasFile = file != null && !file.isEmpty();
    boolean hasCover = cover != null && !cover.isEmpty();
    System.out.println(hasFile);
    System.out.println(hasCover);

    if(hasFile || hasCover){
      // python脚本参数
      Map<String, String> args = new HashMap<>();
      args.put("bookName", bookDTO.getTitle());
      args.put("overwrite", "true");

      Path contentTempPath = null;
      Path coverTempPath = null;
      try {
        // 临时存储路径
        Path projectDir = Path.of(uploadTmpConfig.getBaseDir());
        contentTempPath = projectDir.resolve(uploadTmpConfig.getFileUploads()).resolve(UUID.randomUUID() + ".pdf");
        coverTempPath = projectDir.resolve(uploadTmpConfig.getCoverUploads()).resolve(UUID.randomUUID() + ".jpg");

        if(hasCover){
          // 校验封面图片格式
          String originalFilename = Objects.requireNonNull(cover.getOriginalFilename());
          if (!originalFilename.endsWith(".jpg") && !originalFilename.endsWith(".jpeg") && !originalFilename.endsWith(".png")) {
            throw new BookUploadException("书籍封面暂仅支持jpg、jpeg、png格式", 400);
          }
          // 临时存储封面
          try {
            cover.transferTo(coverTempPath);
          } catch (IOException e) {
            throw new BookUploadException("封面上传失败", 500);
          }
          // 传递封面图片临时保存本地路径参数
          args.put("coverPath", coverTempPath.toString());
        }

        if(hasFile){
          // 校验电子书pdf格式
          String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
          if(!originalFilename.endsWith(".pdf")) {
            throw new BookUploadException("电子版暂仅支持pdf格式", 400);
          }
          // 临时存储电子书
          try {
            file.transferTo(contentTempPath);
          } catch (IOException e) {
            throw new BookUploadException("电子书上传失败", 500);
          }
          // 传递电子书临时保存本地路径参数
          args.put("filePath", contentTempPath.toString());
        }

        // 调用Python解析
        String result = PythonScriptExecutor.executePythonScript("book_upload.py", args, 10);

        // 添加书籍基本信息
        EbookDTO ebookDTO = JSON.parseObject(result, EbookDTO.class);
        book.setCover(ebookDTO.getCover_path());
        book.setEbook(ebookDTO.getOriginal_path());
      } finally {
        // 清理临时文件
        try {
          if (contentTempPath != null) Files.deleteIfExists(contentTempPath);
          if (coverTempPath != null) Files.deleteIfExists(coverTempPath);
        } catch (IOException e) {
          log.error("临时文件删除失败", e);
        }
      }
    }

    return bookService.addBook(book, bookDTO.getBookNumber());
  }

  @PostMapping("/updateBook")
  public ResponseData updateBook(
          @RequestParam("bookId")
          @NotBlank(message = "id不能为空")
          String id,
          @RequestPart("bookData")
          @Valid
          BookDTO bookDTO
  ){
    Integer bookId = ParseUtil.StringIdParseInteger(id);
    Book book = bookDTO.toEntity();
    book.setId(bookId);
    return bookService.updateBook(book);
  }

  @PostMapping("/deleteBook")
  public ResponseData deleteBook(
          @RequestParam("bookId")
          @NotBlank(message = "id不能为空")
          String id
  ) {
    Integer bookId = ParseUtil.StringIdParseInteger(id);
    return bookService.deleteBook(bookId);
  }

  @PostMapping("/setBookNumber")
  public ResponseData setBookNumber(
          @RequestParam("bookId")
          @NotBlank(message = "id不能为空")
          String id,
          @RequestParam("bookNumber")
          @NotBlank(message = "书本库存不能为空")
          String bk
  ){
    Integer bookId = ParseUtil.StringIdParseInteger(id);
    Integer bookNumber = ParseUtil.StringIdParseInteger(bk, "书本库存");
    return bookService.setBookNumber(bookId, bookNumber);
  }


}
