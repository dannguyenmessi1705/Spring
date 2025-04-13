package com.didan.clientgrpc;

import com.google.protobuf.Descriptors;
import java.util.List;
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

  @GetMapping("/book/{authorId}")
  public List<Map<Descriptors.FieldDescriptor, Object>> getBooksByAythor(@PathVariable("authorId") String authorId) throws InterruptedException {
    return bookAuthorClientService.getBooksByAuthor(Integer.parseInt(authorId));
  } // Gọi hàm getBooksByAuthor trên client và trả về danh sách sách của tác giả này

  @GetMapping("/book")
  public Map<String, Map<Descriptors.FieldDescriptor, Object>> getExpensiveBook() throws InterruptedException {
    return bookAuthorClientService.getExpensiveBook();
  } // Gọi hàm getExpensiveBook trên client và trả về sách đắt nhất của tác giả này

  @GetMapping("/book/author/{gender}")
  public List<Map<Descriptors.FieldDescriptor, Object>> getBookByAuthorGender(@PathVariable String gender) throws InterruptedException {
    return bookAuthorClientService.getBooksByAuthorGender(gender);
  } // Gọi hàm getBookByAuthorGender trên client và trả về danh sách sách của tác giả này
}
