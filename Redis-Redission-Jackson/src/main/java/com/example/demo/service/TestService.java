package com.example.demo.service;

import com.example.demo.entity.Author;
import com.example.demo.entity.Book;
import com.example.demo.entity.Chap;
import com.example.demo.utils.ObjectMapperUtils;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Data
@Slf4j
public class TestService {
  private final CacheService cacheService;

  public void uploadRedis() {
    log.info("Start upload data to Redis");
    List<Chap> chaps = List.of(
        new Chap("Chap 1", 1, "Content 1"),
        new Chap("Chap 2", 2, "Content 2"),
        new Chap("Chap 3", 3, "")
    );
    Book book = new Book("Book 1", 100, 2021, chaps);
    Book book2 = new Book("Book 2", 200, 2022, chaps);
    Author author = new Author("Author 1", 30, "Address 1", List.of(book, book2));

    log.info("Upload [JSON {}] to Redis", ObjectMapperUtils.toJson(author));
    cacheService.setCache("author", "author", author, 1000);
    log.info("End upload data to Redis");
  }

  public Author getRedis() {
    log.info("Start get data from Redis");
    Author author = cacheService.getCache("author", "author", Author.class);
    log.info("Get [Object {}] from Redis", author);
    log.info("End get data from Redis");
    return author;
  }

  public void deleteRedis() {
    log.info("Start delete data from Redis");
    cacheService.deleteCache("author", "author");
    log.info("End delete data from Redis");
  }
}
