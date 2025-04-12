package com.didan.server;

import com.didan.Author;
import com.didan.BookAuthorServiceGrpc;
import com.didan.proto.TempDB;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class BookAuthorServerService extends BookAuthorServiceGrpc.BookAuthorServiceImplBase {

  @Override
  public void getAuthor(Author request, StreamObserver<Author> responseObserver) {
    TempDB.getAuthorsFromTempDb().stream().filter(
            author -> author.getAuthorId() == request.getAuthorId())
        .findFirst() // Lấy tác giả đầu tiên có authorId trùng với request
        .ifPresent(responseObserver::onNext); // onNext sẽ gửi tác giả này về cho client
    responseObserver.onCompleted(); // Kết thúc cuộc gọi
  }
}
