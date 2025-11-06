package com.didan.reactive.learn.router.validator;

import com.didan.reactive.learn.router.dto.CustomerDto;
import com.didan.reactive.learn.router.exception.ApplicationException;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import reactor.core.publisher.Mono;

public class RequestValidator {

  private static Predicate<CustomerDto> hasName() {
    return dto -> Objects.nonNull(dto.name());
  } // Kiểm tra tên không null hoặc rỗng, Predicate<CustomerDto> là một hàm nhận vào CustomerDto và trả về boolean

  private static Predicate<CustomerDto> hasValidEmail() {
    return dto -> Objects.nonNull(dto.email()) && dto.email().contains("@");
  } // Kiểm tra email không null và có chứa ký tự '@', Predicate<CustomerDto> là một hàm nhận vào CustomerDto và trả về boolean

  public static UnaryOperator<Mono<CustomerDto>> validate() {
    return mono -> mono
        .filter(hasName()) // Lọc các CustomerDto có tên hợp lệ
        .switchIfEmpty(ApplicationException.misssingName()) // Nếu không có CustomerDto nào hợp lệ, trả về lỗi tên bị thiếu
        .filter(hasValidEmail()) // Lọc các CustomerDto có email hợp lệ
        .switchIfEmpty(ApplicationException.missingValidEmail()); // Nếu không có CustomerDto nào hợp lệ, trả về lỗi email không hợp lệ
  } // UnaryOperator<Mono<CustomerDto>> là một hàm nhận vào Mono<CustomerDto> và trả về Mono<CustomerDto> sau khi đã kiểm tra các điều kiện
}
