package com.example.demo.controller;

import com.example.demo.entity.Author;
import com.example.demo.service.TestService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TestController {

  private final TestService testService;

  @PostMapping("/upload")
  public void upload() {
    testService.uploadRedis();
  }

  @GetMapping("/get")
  public ResponseEntity<Author> get() {
    Author author = testService.getRedis();
    return ResponseEntity.ok(author);
  }

  @DeleteMapping("/delete")
  public void delete() {
    testService.deleteRedis();
  }
}
