package com.didan.reactive.learn.updownstream.controller;

import com.didan.reactive.learn.updownstream.dto.ProductDto;
import com.didan.reactive.learn.updownstream.dto.UploadResponse;
import com.didan.reactive.learn.updownstream.service.ProductService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("products")
public class ProductController {

  private final ProductService productService;

  @PostMapping(
      value = "upload", // URL endpoint để tải lên sản phẩm
      consumes = MediaType.APPLICATION_NDJSON_VALUE // Chỉ định rằng endpoint này chấp nhận dữ liệu định dạng NDJSON (Tức là Newline Delimited JSON, các đối tượng JSON được phân tách bằng dòng mới) (Chỉ định định dạng này để xử lý luồng dữ liệu lớn, giữa các server với server hoặc client với server)
  )
  public Mono<UploadResponse> uploadProducts(@RequestBody Flux<ProductDto> flux) {
    log.info("invoke uploadProducts");
    return this.productService.saveProducts(flux)
        .then(this.productService.getProductCount())
        .map(count -> new UploadResponse(UUID.randomUUID(), count));
  }

  @GetMapping(
      value = "download", // URL endpoint để tải về sản phẩm
      produces = MediaType.APPLICATION_NDJSON_VALUE // Chỉ định rằng endpoint này trả về dữ liệu định dạng NDJSON (Tức là Newline Delimited JSON, các đối tượng JSON được phân tách bằng dòng mới, thường dùng để truyền tải luồng dữ liệu lớn)
  )
  public Flux<ProductDto> downloadProducts() {
    log.info("invoke downloadProducts");
    return this.productService.getAllProducts();
  }

  @PostMapping
  public Mono<ProductDto> saveProduct(@RequestBody Mono<ProductDto> mono) {
    return this.productService.saveProduct(mono); // Lưu sản phẩm mới và phát luồng dữ liệu đến các subscriber
  }

  @GetMapping(value = "/stream/{maxPrice}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ProductDto> productStream(@PathVariable Integer maxPrice) {
    return this.productService.productStream() // Lấy luồng dữ liệu sản phẩm từ sink
        .filter(dto -> dto.price() <= maxPrice); // Lọc sản phẩm theo giá tối đa được chỉ định trong đường dẫn
  }
}
