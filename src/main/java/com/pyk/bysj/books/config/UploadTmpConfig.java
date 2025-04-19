package com.pyk.bysj.books.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.upload.tmp")
public class UploadTmpConfig {
  private String baseDir;
  private String fileUploads;
  private String coverUploads;
}