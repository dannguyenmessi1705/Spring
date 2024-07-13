package com.didan.microservices.accounts.controller;

import com.didan.microservices.accounts.dto.CustomerDetailsDto;
import com.didan.microservices.accounts.dto.CustomerDto;
import com.didan.microservices.accounts.dto.ErrorDto;
import com.didan.microservices.accounts.service.ICustomerDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
@Validated
@Tag(
    name = "REST APIs for Customer Details",
    description = "Customer details"
)
public class CustomerDetailsController {

  Logger logger = LoggerFactory.getLogger(getClass());
  private final ICustomerDetailsService iCustomerDetailsService;

  public CustomerDetailsController(ICustomerDetailsService iCustomerDetailsService) {
    this.iCustomerDetailsService = iCustomerDetailsService;
  }
  @Operation(
      summary = "Fetch Customer Details REST API",
      description = "REST API to fetch Customer details based on a mobile number",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "HTTP Status OK"
          ),
          @ApiResponse(
              responseCode = "500",
              description = "HTTP Status Internal Server Error",
              content = @Content(
                  schema = @Schema(implementation = ErrorDto.class)
              )
          )
      }
  )
  @GetMapping("/fetch-customer")
  public ResponseEntity<? super CustomerDetailsDto> fetch(
      @RequestHeader("bank-correlation-id") String correlationId, // Lấy giá trị từ Header
      @RequestParam
      @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
      String mobile) {
    logger.debug("Bank-correlation-id generated in Accounts Service : {}", correlationId);
    CustomerDetailsDto customerDetailsDto = iCustomerDetailsService.fetchCustomerDetails(correlationId, mobile);
    return ResponseEntity.status(HttpStatus.OK)
        .body(customerDetailsDto);
  }
}
