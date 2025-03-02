package com.didan.migrateflyway_spring.controller;

import com.didan.migrateflyway_spring.entity.Books;
import com.didan.migrateflyway_spring.service.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {
  private final BookService bookService;

  @GetMapping("/all")
  public ResponseEntity<List<Books>> getAllBooks() {
    return ResponseEntity.ok(bookService.getAllBooks());
  }
}
