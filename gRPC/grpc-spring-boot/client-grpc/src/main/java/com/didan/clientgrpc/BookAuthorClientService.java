package com.didan.clientgrpc;

import com.didan.Author;
import com.didan.BookAuthorServiceGrpc;
import com.google.protobuf.Descriptors;
import java.util.Map;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class BookAuthorClientService {
  @GrpcClient("grpc-didan-service")
  BookAuthorServiceGrpc.BookAuthorServiceBlockingStub synchronousClient; // Dạng client đồng bộ (unary - synchronous)

  public Map<Descriptors.FieldDescriptor, Object> getAuthor(int authorId) {
    Author authorRequest = Author.newBuilder()
        .setAuthorId(authorId)
        .build(); // Tạo request với authorId là authorId truyền vào
    Author authorResponse = synchronousClient.getAuthor(authorRequest); // Gọi hàm getAuthor trên server và nhận response
    return authorResponse.getAllFields(); // Trả về tất cả các trường của đối tượng Author
  }
}
