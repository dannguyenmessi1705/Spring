package com.didan.microservices.gatewaysever.filter;

import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component // Thêm Bean vào Spring IoC container
public class FilterUtility {  // Lớp FilterUtility chứa các phương thức xử lý logic
  public static final String CORRELATION_ID = "bank-correlation-id"; // id của correlation trong header
  public String getCorrelationId(HttpHeaders requestHeaders) { // Phương thức lấy ra correlation-id từ headers
    if (requestHeaders.get(CORRELATION_ID) != null) { // Kiểm tra xem trường correlation-id đã tồn tại trong headers hay chưa
      List<String> requestHeaderList = requestHeaders.get(CORRELATION_ID); // Lấy ra giá trị của trường correlation-id
      return requestHeaderList.stream().findFirst().get(); // Trả về giá trị của trường correlation-id
    } else {
      return null; // Nếu trường correlation-id chưa tồn tại thì trả về null
    }
  }

  public ServerWebExchange setRequestHeader(ServerWebExchange exchange, String name, String value) {
    return exchange.mutate()
        .request(exchange.getRequest().mutate().header(name, value).build()) // Tạo lại request với header mới
        .build(); // Xây dựng exchange mới
  } // Phương thức setRequestHeader giúp thêm một header vào request

  public ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
    return this.setRequestHeader(exchange, CORRELATION_ID, correlationId);
  } // Phương thức setCorrelationId giúp thêm correlation-id vào request

}