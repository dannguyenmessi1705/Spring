package com.didan.reactive.learn.router.exception;

import reactor.core.publisher.Mono;

public class ApplicationException extends RuntimeException {

  public static <T> Mono<T> customerNotFound(Integer id) {
    return Mono.error(new CustomerNotFoundException(id)); // Trả về lỗi CustomerNotFoundException nếu không tìm thấy khách hàng
  }

  public static <T> Mono<T> misssingName() {
    return Mono.error(new InvalidInputException("Name is required")); // Trả về lỗi CustomerNotFoundException nếu không tìm thấy khách hàng
  }

  public static <T> Mono<T> missingValidEmail() {
    return Mono.error(new InvalidInputException("Valid email is required")); // Trả về lỗi CustomerNotFoundException nếu không tìm thấy khách hàng
  }

}
