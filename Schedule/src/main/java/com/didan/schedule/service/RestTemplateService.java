package com.didan.schedule.service;

import java.util.Collections;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestTemplateService {
  private final RestTemplate restTemplate;

  public <T> ResponseEntity<T> process(HttpMethod method, String url, HttpHeaders headers, Objects requestBody, ParameterizedTypeReference<T> responseType) {
    if (headers == null) {
      headers = new HttpHeaders();
      headers.put(HttpHeaders.CONTENT_TYPE, Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));
      headers.put(HttpHeaders.ACCEPT, Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));
    }
    try {
      return restTemplate.exchange(url, method, new HttpEntity<>(requestBody, headers), responseType);
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      log.error("Http Client Error", ex);
      return ResponseEntity.status(ex.getStatusCode()).headers(ex.getResponseHeaders()).body(null);
    } catch (ResourceAccessException ex) {
      log.error("Resource Access Exception", ex);
      return ResponseEntity.status(ex.getMessage().contains("Read timed out") ? 504 : 503).body(null);
    }
  }
}
