package com.didan.reactive.learn.crud;

import com.didan.reactive.learn.crud.dto.CustomerDto;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@AutoConfigureWebTestClient
@SpringBootTest(properties = "package=crud")
@Slf4j
public class CusomerServiceTest {

  @Autowired
  private WebTestClient client; // Khách hàng WebTestClient để thực hiện các yêu cầu HTTP trong bài kiểm tra

  @Test
  public void allCustomers() {
    client.get() // Tạo yêu cầu GET
        .uri("/customers") // Đặt URI cho yêu cầu
        .exchange() // Thực hiện yêu cầu
        .expectStatus().is2xxSuccessful() // Kiểm tra mã trạng thái HTTP trả về là 2xx
        .expectHeader().contentType(MediaType.APPLICATION_JSON) // Kiểm tra header Content-Type là application/json
        .expectBodyList(CustomerDto.class) // Kỳ vọng phản hồi là một danh sách các đối tượng CustomerDto
        .value(list -> log.info("Customers: {}", list)) // Ghi log danh sách khách hàng nhận được
        .hasSize(10) // Kiểm tra kích thước của danh sách là 10
        .consumeWith(response -> log.info("Response: {}", response.getResponseBody())); // Ghi log phản hồi nhận được
  }

  @Test
  public void paginatedCustomers() {
    this.client.get()
        .uri("/customers/paginated?page=3&size=2") // Gọi API với tham số phân trang page=3 và size=2
        .exchange() // Thực hiện yêu cầu
        .expectStatus().is2xxSuccessful() // Kiểm tra mã trạng thái HTTP trả về là 2xx
        .expectBody() // Kỳ vọng phản hồi
        .consumeWith(r -> log.info("{}", new String(Objects.requireNonNull(r.getResponseBody())))) // Ghi log phản hồi nhận được
        .jsonPath("$.length()").isEqualTo(2) // Kiểm tra độ dài mảng phản hồi là 2 vơi `$` đại diện cho json root
        .jsonPath("$[0].id").isEqualTo(7) // Kiểm tra phần tử đầu tiên có id là 5 với `$` đại diện cho json root
        .jsonPath("$[1].id").isEqualTo(8); // Kiểm tra phần tử thứ hai có id là 6 với `$` đại diện cho json root
  }

  @Test
  public void customerById() {
    this.client.get()
        .uri("/customers/1") // Gọi API để lấy khách hàng có ID là 1
        .exchange() // Thực hiện yêu cầu
        .expectStatus().is2xxSuccessful() // Kiểm tra mã trạng thái HTTP trả về là 2xx
        .expectBody() // Kỳ vọng phản hồi
        .consumeWith(r -> log.info("{}", new String(Objects.requireNonNull(r.getResponseBody())))) // Ghi log phản hồi nhận được
        .jsonPath("$.id").isEqualTo(1) // Kiểm tra id của khách hàng trả về là 1 với `$` đại diện cho json root
        .jsonPath("$.name").isEqualTo("sam") // Kiểm tra tên của khách hàng trả về là "sam" với `$` đại diện cho json root
        .jsonPath("$.email").isEqualTo("sam@gmail.com"); // Kiểm tra email của khách hàng trả về, với `$` đại diện cho json root
  }

  @Test
  public void createAndDeleteCustomer() {
    // create
    var dto = new CustomerDto(null, "marshal", "marshal@gmail.com"); // Tạo đối tượng CustomerDto mới để gửi trong yêu cầu tạo khách hàng
    this.client.post() // Tạo yêu cầu POST
        .uri("/customers")// Đặt URI cho yêu cầu
        .bodyValue(dto) // Đặt thân yêu cầu với đối tượng CustomerDto
        .exchange() // Thực hiện yêu cầu
        .expectStatus().is2xxSuccessful() // Kiểm tra mã trạng thái HTTP trả về là 2xx
        .expectBody() // Kỳ vọng phản hồi
        .consumeWith(r -> log.info("{}", new String(Objects.requireNonNull(r.getResponseBody())))) // Ghi log phản hồi nhận được
        .jsonPath("$.id").isEqualTo(11) // Kiểm tra id của khách hàng trả về là 11 với `$` đại diện cho json root
        .jsonPath("$.name").isEqualTo("marshal") // Kiểm tra tên của khách hàng trả về là "marshal" với `$` đại diện cho json root
        .jsonPath("$.email").isEqualTo("marshal@gmail.com"); // Kiểm tra email của khách hàng trả về, với `$` đại diện cho json root

    // delete
    this.client.delete() // Tạo yêu cầu DELETE
        .uri("/customers/11") // Đặt URI cho yêu cầu để xoá khách hàng có ID là 11
        .exchange() // Thực hiện yêu cầu
        .expectStatus().is2xxSuccessful() // Kiểm tra mã trạng thái HTTP trả về là 2xx
        .expectBody().isEmpty(); // Kỳ vọng phản hồi là rỗng
  }

  @Test
  public void updateCustomer() {
    var dto = new CustomerDto(null, "noel", "noel@gmail.com"); // Tạo đối tượng CustomerDto mới để gửi trong yêu cầu cập nhật khách hàng
    this.client.put() // Tạo yêu cầu PUT
        .uri("/customers/10") // Đặt URI cho yêu cầu để cập nhật khách hàng có ID là 10
        .bodyValue(dto) // Đặt thân yêu cầu với đối tượng CustomerDto
        .exchange() // Thực hiện yêu cầu
        .expectStatus().is2xxSuccessful() // Kiểm tra mã trạng thái HTTP trả về là 2xx
        .expectBody() // Kỳ vọng phản hồi
        .consumeWith(r -> log.info("{}", new String(Objects.requireNonNull(r.getResponseBody())))) // Ghi log phản hồi nhận được
        .jsonPath("$.id").isEqualTo(10) // Kiểm tra id của khách hàng trả về là 10 với `$` đại diện cho json root
        .jsonPath("$.name").isEqualTo("noel") // Kiểm tra tên của khách hàng trả về là "noel" với `$` đại diện cho json root
        .jsonPath("$.email").isEqualTo("noel@gmail.com"); // Kiểm tra email của khách hàng trả về, với `$` đại diện cho json root
  }

  @Test
  public void customerNotFound() {
    // get
    this.client.get()
        .uri("/customers/11")
        .exchange()
        .expectStatus().is4xxClientError()
        .expectBody().isEmpty();

    // delete
    this.client.delete()
        .uri("/customers/11")
        .exchange()
        .expectStatus().is4xxClientError()
        .expectBody().isEmpty();

    // put
    var dto = new CustomerDto(null, "noel", "noel@gmail.com");
    this.client.put()
        .uri("/customers/11")
        .bodyValue(dto)
        .exchange()
        .expectStatus().is4xxClientError()
        .expectBody().isEmpty();
  }

}

/**
 * > Trong `WebClient` test, sử dụng `jsonPath` để kiểm tra các giá trị cụ thể trong phản hồi JSON. Dấu `$` đại diện cho gốc của tài liệu JSON, và bạn có thể sử dụng các biểu thức `jsonPath` để truy cập và kiểm tra các trường cụ thể trong đối tượng JSON trả về.
 * > Với các method có yêu cầu requet body như POST và PUT, có thể sử dụng 2 phương thức:
 * > - `bodyValue(Object body)`: Phương thức này được sử dụng khi bạn có một đối tượng cụ thể mà bạn muốn gửi trong thân yêu cầu. Đối tượng này sẽ được tự động chuyển đổi thành định dạng JSON (hoặc định dạng khác tùy thuộc vào cấu hình của bạn) trước khi gửi đi.
 * > - `body(Mono<? extends T> body, Class<T> elementClass)`: Phương thức này được sử dụng khi bạn có một `Mono` (hoặc `Flux`) chứa đối tượng mà bạn muốn gửi trong thân yêu cầu. Điều này hữu ích trong các tình huống phản ứng, nơi dữ liệu có thể không có sẵn ngay lập tức và bạn muốn gửi nó khi nó trở nên có sẵn.
 */