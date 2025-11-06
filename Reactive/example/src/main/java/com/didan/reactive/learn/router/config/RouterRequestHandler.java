package com.didan.reactive.learn.router.config;

import com.didan.reactive.learn.router.dto.CustomerDto;
import com.didan.reactive.learn.router.service.CustomerService;
import com.didan.reactive.learn.router.validator.RequestValidator;
import com.didan.reactive.learn.validation.exception.ApplicationException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RouterRequestHandler {

  private final CustomerService customerService;

  /**
   * Các handler xử lý request này luôn trả về Mono<ServerResponse> ServerRequest có thể lấy thông tin từ request như path variable, query param, body, header, ...
   *
   * @param request
   * @return
   */
  public Mono<ServerResponse> getAllCustomers(ServerRequest request) {
    Flux<CustomerDto> customerDtoFlux = customerService.getAllCustomers(); // Lấy tất cả khách hàng từ service
    return customerDtoFlux.as(flux -> ServerResponse.ok().body(flux, CustomerDto.class)); // Trả về phản hồi với danh sách khách hàng
  }

  public Mono<ServerResponse> pagniatedCustomers(ServerRequest request) {
    var page = request
        .queryParam("page") // Lấy tham số truy vấn "page"
        .map(Integer::parseInt) // Chuyển đổi giá trị chuỗi sang Integer do queryParam luôn trả về Optional<String>
        .orElse(1); // Mặc định là trang 1 nếu không có tham số
    var size = request
        .queryParam("size") // Lấy tham số truy vấn "size"
        .map(Integer::parseInt) // Chuyển đổi giá trị chuỗi sang Integer do queryParam luôn trả về Optional<String>
        .orElse(3); // Mặc định là kích thước 3 nếu không có tham số
    Mono<List<CustomerDto>> listMono = customerService.getAllCustomers(page, size); // Lấy danh sách khách hàng phân trang từ service
    return listMono.flatMap(mono -> ServerResponse.ok().bodyValue(mono)); // Trả về phản hồi với danh sách khách hàng, dùng flatMap để tránh lồng Mono (dữ liệu đã là Mono<List<CustomerDto>> -> flatMap sẽ lấy ra List<CustomerDto>)
  }

  public Mono<ServerResponse> getCustomerById(ServerRequest request) {
    var id = Integer.parseInt(request.pathVariable("id")); // Lấy path variable "id" và chuyển đổi sang Integer, do pathVariable luôn trả về String
    Mono<CustomerDto> customerDtoMono = customerService.getCustomerById(id) // Lấy khách hàng theo ID từ service
        .switchIfEmpty(ApplicationException.customerNotFound(id)); // Nếu không tìm thấy khách hàng, trả về lỗi CustomerNotFoundException
    return customerDtoMono.flatMap(customerDto -> ServerResponse.ok().bodyValue(customerDto)); // Trả về phản hồi với khách hàng, dùng flatMap để tránh lồng Mono (dữ liệu đã là Mono<CustomerDto> -> flatMap sẽ lấy ra CustomerDto)
  }

  public Mono<ServerResponse> createCustomer(ServerRequest request) {
    Mono<CustomerDto> customerDtoMono = request.bodyToMono(CustomerDto.class); // Lấy body từ request và chuyển đổi sang Mono<CustomerDto>
    Mono<CustomerDto> validCustomerDtoMono = customerDtoMono.transform(RequestValidator.validate()); // Áp dụng bộ validator để kiểm tra dữ liệu đầu vào
    Mono<CustomerDto> savedCustomerDtoMono = validCustomerDtoMono.as(customerService::saveCustomer); // Lưu khách hàng nếu dữ liệu hợp lệ
    return savedCustomerDtoMono.flatMap(savedCustomerDto -> ServerResponse.ok().bodyValue(savedCustomerDto)); // Trả về phản hồi với khách hàng đã lưu, dùng flatMap để tránh lồng Mono
  }

  public Mono<ServerResponse> updateCustomer(ServerRequest request) {
    var id = Integer.parseInt(request.pathVariable("id"));
    Mono<CustomerDto> customerDtoMono = request.bodyToMono(CustomerDto.class); // Lấy body từ request và chuyển đổi sang Mono<CustomerDto>
    Mono<CustomerDto> validCustomerDtoMono = customerDtoMono.transform(RequestValidator.validate()); // Áp dụng bộ validator để kiểm tra dữ liệu đầu vào
    Mono<CustomerDto> updatedCustomer = validCustomerDtoMono
        .as(validRequest -> customerService.updateCustomer(id, validRequest)) // Cập nhật khách hàng nếu dữ liệu hợp lệ, dua vào id và dữ liệu đã được kiểm tra
        .switchIfEmpty(ApplicationException.customerNotFound(id)); // Nếu không tìm thấy khách hàng để cập nhật, trả về lỗi CustomerNotFoundException
    return updatedCustomer.flatMap(updatedDto -> ServerResponse.ok().bodyValue(updatedDto)); // Trả về phản hồi với khách hàng đã cập nhật, dùng flatMap để tránh lồng Mono
  }

  public Mono<ServerResponse> deleteCustomer(ServerRequest request) {
    var id = Integer.parseInt(request.pathVariable("id"));
    Mono<Boolean> deleteResult = customerService.deleteCustomer(id) // Xóa khách hàng theo id
        .filter(b -> b) // Lọc kết quả trả về, chỉ tiếp tục nếu khách hàng đã được xóa (b == true)
        .switchIfEmpty(ApplicationException.customerNotFound(id)); // Nếu không tìm thấy khách hàng để xóa, trả về lỗi CustomerNotFoundException
    return deleteResult.then(ServerResponse.ok().build()); // Trả về phản hồi rỗng với mã trạng thái OK
  }
}
