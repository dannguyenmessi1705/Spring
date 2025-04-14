package com.didan.grpc.clientfile.controller;

import com.didan.grpc.clientfile.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileUploadController {

  private final FileUploadService fileUploadService;

  @PostMapping("/upload")
  public String uploadFile(@RequestParam("file")MultipartFile multipartFile) {
    return fileUploadService.uploadFile(multipartFile);
  }
}
