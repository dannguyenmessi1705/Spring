package com.didan.pattern.microservices_pattern.orchestrator_sequence.client;

import com.didan.pattern.microservices_pattern.orchestrator_sequence.dto.InventoryRequest;
import com.didan.pattern.microservices_pattern.orchestrator_sequence.dto.InventoryResponse;
import com.didan.pattern.microservices_pattern.orchestrator_sequence.dto.Status;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class InventoryClient {

  private final WebClient webClient;

  private static final String DEDUCT = "deduct";
  private static final String RESTORE = "restore";

  public InventoryClient(@Value("${orchestrator_sequence.inventory.service}") String inventoryServiceUrl) {
    this.webClient = WebClient.builder()
        .baseUrl(inventoryServiceUrl)
        .build();
  }

  /**
   * Hàm gọi service Inventory
   *
   * @param endpoint
   * @param request
   * @return
   */
  private Mono<InventoryResponse> callInventoryService(String endpoint, InventoryRequest request) {
    return webClient
        .post() // Phương thức HTTP POST
        .uri(endpoint) // Đường dẫn endpoint
        .bodyValue(request) // Gửi dữ liệu request trong body
        .retrieve() // Thực hiện yêu cầu và lấy phản hồi
        .bodyToMono(InventoryResponse.class) // Chuyển đổi phản hồi thành Mono<InventoryResponse>
        .onErrorReturn(buildFallbackResponse(request)); // Xử lý lỗi và trả về phản hồi dự phòng
  }

  /**
   * Hàm fallback khi gọi service Inventory thất bại
   *
   * @param request
   * @return
   */
  private InventoryResponse buildFallbackResponse(InventoryRequest request) {
    return InventoryResponse.create(
        null,
        request.getProductId(),
        request.getQuantity(),
        null,
        Status.FAILED
    );
  }

  /**
   * Hàm trừ tồn kho
   *
   * @param request
   * @return
   */
  public Mono<InventoryResponse> deduct(InventoryRequest request) {
    return callInventoryService(DEDUCT, request);
  }

  /**
   * Hàm hoàn trả tồn kho
   *
   * @param request
   * @return
   */
  public Mono<InventoryResponse> restore(InventoryRequest request) {
    return callInventoryService(RESTORE, request);
  }
}
