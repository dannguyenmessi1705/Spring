package com.didan.sequencedatabase.controller;

import com.didan.sequencedatabase.cache.SequenceCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/sequence")
public class GenIdController {

  private final SequenceCache sequenceCache;

  @GetMapping("/genId")
  public String getId() {
    return sequenceCache.generateTransactionId(8);
  }
}
