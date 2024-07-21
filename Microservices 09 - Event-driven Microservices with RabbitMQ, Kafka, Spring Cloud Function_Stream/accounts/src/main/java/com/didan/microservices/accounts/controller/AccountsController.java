package com.didan.microservices.accounts.controller;

import com.didan.microservices.accounts.constant.AccountsConstant;
import com.didan.microservices.accounts.dto.AccountsContactInfoDto;
import com.didan.microservices.accounts.dto.CustomerDto;
import com.didan.microservices.accounts.dto.ErrorDto;
import com.didan.microservices.accounts.dto.ResponseDto;
import com.didan.microservices.accounts.service.IAccountsService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Tag(
    name = "CRUD REST APIs for Accounts",
    description = "CRUD REST APIs to CREATE, UPDATE, FETCH AND DELETE account details"
)
public class AccountsController {

  private final IAccountsService accountsService;
  private final Environment env;
  private final AccountsContactInfoDto contactInfoDto;

  @Value("${build.version}")
  private String buildVersion;

  public AccountsController(IAccountsService accountsService, Environment env,
      AccountsContactInfoDto contactInfoDto) {
    this.accountsService = accountsService;
    this.env = env;
    this.contactInfoDto = contactInfoDto;
  }

  @Operation(
      summary = "Create Account REST API",
      description = "REST API to create new Customer &  Account",
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "HTTP Status CREATED"
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
  @PostMapping("/create")
  public ResponseEntity<? super CustomerDto> createAccount(
      @Valid @RequestBody CustomerDto customerDto) {
    accountsService.createAccounts(customerDto);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(new ResponseDto(AccountsConstant.STATUS_201, AccountsConstant.MESSAGE_201));
  }

  @Operation(
      summary = "Fetch Account Details REST API",
      description = "REST API to fetch Customer &  Account details based on a mobile number",
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
  @GetMapping("/fetch")
  public ResponseEntity<? super CustomerDto> fetch(
      @RequestParam
      @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
      String mobile) {
    CustomerDto customerDto = accountsService.fetch(mobile);
    return ResponseEntity.status(HttpStatus.OK)
        .body(customerDto);
  }

  @Operation(
      summary = "Update Account Details REST API",
      description = "REST API to update Customer &  Account details based on a account number",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "HTTP Status OK"
          ),
          @ApiResponse(
              responseCode = "417",
              description = "Expectation Failed"
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
  @PatchMapping("/update")
  public ResponseEntity<?> update(@Valid @RequestBody CustomerDto customerDto) {
    boolean isUpdated = accountsService.update(customerDto);
    if (isUpdated) {
      return ResponseEntity.status(HttpStatus.OK)
          .body(new ResponseDto(AccountsConstant.STATUS_200, AccountsConstant.MESSAGE_200));
    } else {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ResponseDto(AccountsConstant.STATUS_417, AccountsConstant.MESSAGE_417_UPDATE));
    }
  }

  @Operation(
      summary = "Delete Account & Customer Details REST API",
      description = "REST API to delete Customer &  Account details based on a mobile number",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "HTTP Status OK"
          ),
          @ApiResponse(
              responseCode = "417",
              description = "Expectation Failed"
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
  @DeleteMapping("/delete")
  public ResponseEntity<?> delete(
      @RequestParam
      @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
      String mobile) {
    boolean isDeleted = accountsService.delete(mobile);
    if (isDeleted) {
      return ResponseEntity.status(HttpStatus.OK)
          .body(new ResponseDto(AccountsConstant.STATUS_200, AccountsConstant.MESSAGE_200));
    } else {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ResponseDto(AccountsConstant.STATUS_417, AccountsConstant.MESSAGE_417_DELETE));
    }
  }

  @Operation(
      summary = "Get Build information",
      description = "Get Build information that is deployed into accounts microservice",
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
  @Retry(name = "getVersion", fallbackMethod = "getVersionFallback")
  @GetMapping("build-info")
  public ResponseEntity<? super String> getVersion() {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(buildVersion);
  }

  public ResponseEntity<? super String> getVersionFallback(Throwable throwable) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body("0.9");
  }

  @Operation(
      summary = "Get Java version",
      description = "Get Java versions details that is installed into accounts microservice",
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

  @RateLimiter(name = "getJavaVersion", fallbackMethod = "getJavaVersionFallback")
  @GetMapping("java-version")
  public ResponseEntity<? super String> getJavaVersion() {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(env.getProperty("JAVA_HOME"));
  }
  public ResponseEntity<? super String> getJavaVersionFallback(Throwable throwable) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(null);
  }


  @Operation(
      summary = "Get Contact info",
      description = "Contact Info details that can be reached out in case of any issues",
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
  @GetMapping("contact-info")
  public ResponseEntity<? super String> getContactInfo() {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(contactInfoDto);
  }
}
