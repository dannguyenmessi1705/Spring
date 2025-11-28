package com.didan.reactive.redissonstartup.fibonacci.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FibService {

  @Cacheable(value = "math:fib", key = "#idx")
  // Cache name = math:fib, dựa trên idx để phân biệt các giá trị khác nhau, mặc định sử dụng hết các tham số của phương thức làm key phân biệt
  public int getFib(int idx, String name) {
    log.info("Calculating fib for idx: {} and name: {}", idx, name);
    return fib(idx);
  }

  @Cacheable()
  private int fib(int idx) {
    if (idx < 2) {
      return idx;
    }
    return fib(idx - 1) + fib(idx - 2);
  }
}
