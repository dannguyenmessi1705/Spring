package com.didan.server;

import com.didan.Author;
import com.didan.Book;
import com.didan.BookAuthorServiceGrpc;
import com.didan.proto.TempDB;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.List;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService // Đánh dấu lớp này là một dịch vụ gRPC
public class BookAuthorServerService extends BookAuthorServiceGrpc.BookAuthorServiceImplBase { // Kế thừa lớp sinh ra từ proto để có thể override các hàm trong đó

  @Override
  public void getAuthor(Author request, StreamObserver<Author> responseObserver) { // Hàm này thể hiện client gửi một request là Author và nhận về một response cũng là Author
    TempDB.getAuthorsFromTempDb().stream().filter(
            author -> author.getAuthorId() == request.getAuthorId())
        .findFirst() // Lấy tác giả đầu tiên có authorId trùng với request
        .ifPresent(responseObserver::onNext); // onNext sẽ gửi tác giả này về cho client
    responseObserver.onCompleted(); // Kết thúc cuộc gọi
  }

  @Override
  public void getBooksByAuthor(Author request, StreamObserver<Book> responseObserver) { // Hàm này thể hiện client gửi một request là Author và nhận về một stream các Book
    TempDB.getBooksFromTempDb()
        .stream()
        .filter(book -> book.getAuthorId() == request.getAuthorId())
        .forEach(responseObserver::onNext); // onNext sẽ gửi tất cả các sách của tác giả này về cho client
    responseObserver.onCompleted(); // Kết thúc cuộc gọi
  }

  @Override
  public StreamObserver<Book> getExpensiveBook(StreamObserver<Book> responseObserver) { // Hàm này thể hiện client gửi một stream các Book và nhận về một Book là sách đắt nhất
    return new StreamObserver<Book>() {
      Book expensiveBook = null; // Biến này dùng để lưu trữ sách đắt nhất
      float priceTrack = 0; // Biến này dùng để lưu trữ giá của sách đắt nhất
      @Override
      public void onNext(Book book) {
        if (book.getPrice() > priceTrack) {
          priceTrack = book.getPrice();
          expensiveBook = book; // Nếu sách này đắt hơn sách đắt nhất thì lưu lại
        }
      } // onNext sẽ được gọi nhiều lần nếu có nhiều sách của tác giả này

      @Override
      public void onError(Throwable throwable) {
        responseObserver.onError(throwable);
      } // onError sẽ được gọi nếu có lỗi xảy ra trong quá trình gọi hàm getBooksByAuthor

      @Override
      public void onCompleted() {
        responseObserver.onNext(expensiveBook); // Gửi sách đắt nhất về cho client
        responseObserver.onCompleted(); // Kết thúc cuộc gọi
      }
    };
  }

  @Override
  public StreamObserver<Book> getBookByAuthorGender(StreamObserver<Book> responseObserver) { // Hàm này thể hiện client gửi một stream các Book và nhận về một stream các Book là sách của tác giả có giới tính tương ứng
    return new StreamObserver<Book>() {
      List<Book> bookList = new ArrayList<>(); // Biến này dùng để lưu trữ sách của tác giả có giới tính tương ứng
      @Override
      public void onNext(Book book) {
        TempDB.getBooksFromTempDb()
            .stream()
            .filter(bookFromDb -> book.getAuthorId() == bookFromDb.getAuthorId())
            .forEach(bookList::add);
      }

      @Override
      public void onError(Throwable throwable) {
        responseObserver.onError(throwable);
      }

      @Override
      public void onCompleted() {
        bookList.forEach(responseObserver::onNext); // Gửi tất cả các sách của tác giả có giới tính tương ứng về cho client
        responseObserver.onCompleted(); // Kết thúc cuộc gọi
      }
    };
  }
}
