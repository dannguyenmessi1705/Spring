package com.didan.migrateflyway_spring.service;

import com.didan.migrateflyway_spring.entity.Books;
import com.didan.migrateflyway_spring.repository.BookRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {
  private final BookRepository bookRepository;

  public List<Books> getAllBooks() {
    return bookRepository.findAll();
  }
}
