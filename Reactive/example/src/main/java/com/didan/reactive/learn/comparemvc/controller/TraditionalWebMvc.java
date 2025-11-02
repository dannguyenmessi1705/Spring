package com.didan.reactive.learn.comparemvc.controller;

import com.didan.reactive.learn.comparemvc.dto.ExampleUserDto;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Flux;

@RestController
@Slf4j
@RequestMapping("traditional")
public class TraditionalWebMvc {

  private final RestClient restClient = RestClient.builder()
      .requestFactory(new JdkClientHttpRequestFactory())
      .baseUrl("https://6698c0eb2069c438cd6fd23e.mockapi.io")
      .build(); // Sử dụng RestClient truyền thống (blocking)

  // Blocking call example using traditional Web MVC approach
  @GetMapping("/users")
  public List<ExampleUserDto> getUsers() {
    log.info("retrieving users using traditional Web MVC approach");
    List<ExampleUserDto> users = restClient
        .get()
        .uri("/api/v1/users")
        .retrieve()
        .body(new ParameterizedTypeReference<>() {
        });
    log.info("Response result: {}", users);
    return users;
  }

  // Blocking call example wrapped in a Flux using traditional Web MVC approach (Lưu ý: Vẫn là blocking call) do sử dụng RestClient truyền thống
  @GetMapping("/users2")
  public Flux<ExampleUserDto> getUsers2() {
    log.info("retrieving users using traditional Web MVC approach");
    List<ExampleUserDto> users = restClient
        .get()
        .uri("/api/v1/users")
        .retrieve()
        .body(new ParameterizedTypeReference<>() {
        });
    log.info("Response result: {}", users);
    return Flux.fromIterable(users);
  }
}
