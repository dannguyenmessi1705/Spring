package com.didan.testperformance.first.config.restemplate;

import java.io.IOException;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

  private final RestTemplateTracer restTemplateTracer;

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    try {
      HttpHeaders headers = request.getHeaders();
      restTemplateTracer.addHeader(headers, HttpHeaders.CONTENT_TYPE, "application/json");
      restTemplateTracer.addHeader(headers, HttpHeaders.ACCEPT, "application/json");
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
      ClientHttpResponse response = execution.execute(request, body);
      restTemplateTracer.trace(request, body, response, new Date());
      return response;
  }
}
