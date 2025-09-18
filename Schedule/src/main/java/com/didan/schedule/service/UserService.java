package com.didan.schedule.service;

import com.didan.schedule.dto.UserResponseDTO;
import com.didan.schedule.utils.CommonUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
  private final RestTemplateService restTemplateService;

  public ResponseEntity<List<UserResponseDTO>> getUsers() {
    String url = "https://68cb71d7716562cf50737151.mockapi.io/api/v1/users/users";
    ParameterizedTypeReference<List<UserResponseDTO>> responseType = new ParameterizedTypeReference<List<UserResponseDTO>>() {};
    log.info("Fetching users from URL: {}", url);
    ResponseEntity<List<UserResponseDTO>> response = restTemplateService.process(HttpMethod.GET, url, null, null, responseType);
    log.info("Received response with status code: {}", response.getStatusCode());
    if (response.hasBody()) {
      log.info("Response Body: {}", CommonUtils.toJson(response.getBody()));
    }
    return response;
  }
}
