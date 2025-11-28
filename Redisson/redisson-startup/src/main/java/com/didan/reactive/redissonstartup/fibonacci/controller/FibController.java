package com.didan.reactive.redissonstartup.fibonacci.controller;

import com.didan.reactive.redissonstartup.fibonacci.service.FibService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("fib")
public class FibController {

  private final FibService fibService;

  @GetMapping("{idx}/{name}")
  public Mono<Integer> getFib(@PathVariable int idx, @PathVariable String name) {
    return Mono.fromSupplier(() -> fibService.getFib(idx, name));
  }

}
