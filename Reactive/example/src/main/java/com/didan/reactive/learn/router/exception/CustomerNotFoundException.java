package com.didan.reactive.learn.router.exception;

public class CustomerNotFoundException extends RuntimeException {

  private static final String MESSAGE = "Customer with id %d not found";

  public CustomerNotFoundException(Integer id) {
    super(MESSAGE.formatted(id));
  }
}
