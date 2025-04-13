package com.didan.clientgrpc;

import com.didan.Author;
import com.didan.Book;
import com.didan.BookAuthorServiceGrpc;
import com.didan.proto.TempDB;
import com.google.protobuf.Descriptors;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BookAuthorClientService {

  @GrpcClient("grpc-didan-service")
  BookAuthorServiceGrpc.BookAuthorServiceBlockingStub synchronousClient; // Dạng client đồng bộ (synchronous)

  @GrpcClient("grpc-didan-service")
  BookAuthorServiceGrpc.BookAuthorServiceStub asynchronousClient; // Dạng bất đồng bộ (asynchronous)

  public Map<Descriptors.FieldDescriptor, Object> getAuthor(int authorId) {
    Author authorRequest = Author.newBuilder()
        .setAuthorId(authorId)
        .build(); // Tạo request với authorId là authorId truyền vào
    Author authorResponse = synchronousClient.getAuthor(authorRequest); // Gọi hàm getAuthor trên server và nhận response
    return authorResponse.getAllFields(); // Trả về tất cả các trường của đối tượng Author
  }

  public List<Map<Descriptors.FieldDescriptor, Object>> getBooksByAuthor(int authorId) throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1); // Tạo latch để đợi cho tất cả các request hoàn thành
    final List<Map<Descriptors.FieldDescriptor, Object>> response = new ArrayList<>();
    Author authorRequest = Author.newBuilder()
        .setAuthorId(authorId)
        .build(); // Tạo request với authorId là authorId truyền vào
    asynchronousClient.getBooksByAuthor(authorRequest, new StreamObserver<Book>() {
      @Override
      public void onNext(Book book) {
        response.add(book.getAllFields());
      } // onNext sẽ được gọi nhiều lần nếu có nhiều sách của tác giả này

      @Override
      public void onError(Throwable throwable) {
        latch.countDown(); // Giải phóng latch nếu có lỗi xảy ra
      } // onError sẽ được gọi nếu có lỗi xảy ra trong quá trình gọi hàm getBooksByAuthor

      @Override
      public void onCompleted() {
        latch.countDown(); // Giải phóng latch khi tất cả các sách của tác giả này đã được gửi về cho client
      } // onCompleted sẽ được gọi khi tất cả các sách của tác giả này đã được gửi về cho client
    });
    boolean await = latch.await(1, TimeUnit.MINUTES); // Đợi cho tất cả các request hoàn thành trong 1 phút
    return await ? response : Collections.emptyList(); // Kiểm tra xem latch có được giải phóng hay không, nếu có thì trả về response, nếu không thì trả về danh sách rỗng
  }

  public Map<String,Map<Descriptors.FieldDescriptor, Object>> getExpensiveBook() throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1); // Tạo latch để đợi cho tất cả các request hoàn thành
    final Map<String,Map<Descriptors.FieldDescriptor, Object>> expensiveBook = new HashMap<>(); // Tạo biến để lưu sách đắt nhất
    StreamObserver<Book> responseObserve = asynchronousClient.getExpensiveBook(new StreamObserver<Book>() {
      @Override
      public void onNext(Book book) {
        expensiveBook.put("ExpensiveBook", book.getAllFields()); // Lưu sách đắt nhất vào biến expensiveBook
      }

      @Override
      public void onError(Throwable throwable) {
        latch.countDown();
      }

      @Override
      public void onCompleted() {
        latch.countDown();
      }
    });
    TempDB.getBooksFromTempDb().forEach(responseObserve::onNext); // Gọi hàm onNext vừa định nghĩa
    responseObserve.onCompleted(); // Gọi hàm onCompleted để kết thúc cuộc gọi
    boolean await = latch.await(1, TimeUnit.MINUTES);
    return await ? expensiveBook : Collections.emptyMap(); // Kiểm tra xem latch có được giải phóng hay không, nếu có thì trả về response, nếu không thì trả về danh sách rỗng
  }

  public List<Map<Descriptors.FieldDescriptor, Object>> getBooksByAuthorGender(String gender) throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1); // Tạo latch để đợi cho tất cả các request hoàn thành
    final List<Map<Descriptors.FieldDescriptor, Object>> response = new ArrayList<>();
    StreamObserver<Book> responseObserver = asynchronousClient.getBookByAuthorGender(new StreamObserver<Book>() {
      @Override
      public void onNext(Book book) {
        response.add(book.getAllFields());
      }

      @Override
      public void onError(Throwable throwable) {
        latch.countDown();
      }

      @Override
      public void onCompleted() {
        latch.countDown();
      }
    });
    TempDB.getAuthorsFromTempDb()
        .stream()
        .filter(author -> author.getGender().equalsIgnoreCase(gender))
        .forEach(author -> responseObserver.onNext(Book.newBuilder().setAuthorId(author.getAuthorId()).build())); // Một stream các tác giả được gửi đến server
    responseObserver.onCompleted(); // Gọi hàm onCompleted để kết thúc cuộc gọi

    boolean await = latch.await(1, TimeUnit.MINUTES); // Đợi cho tất cả các request hoàn thành trong 1 phút
    return await ? response : Collections.emptyList(); // Kiểm tra xem latch có được giải phóng hay không, nếu có thì trả về response, nếu không thì trả về danh sách rỗng
  }
}
