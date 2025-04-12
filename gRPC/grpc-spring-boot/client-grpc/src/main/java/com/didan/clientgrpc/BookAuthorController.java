package com.didan.clientgrpc;

import com.google.protobuf.Descriptors;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookAuthorController {
  private final BookAuthorClientService bookAuthorClientService;

  @GetMapping("/author/{authorId}")
  public Map<Descriptors.FieldDescriptor, Object> getAuthor(@PathVariable("authorId") String authorId) {
    return bookAuthorClientService.getAuthor(Integer.parseInt(authorId));
  }
}
