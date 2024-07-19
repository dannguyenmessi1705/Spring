package com.didan.microservices.accounts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data // lombok annotation to generate getters and setters
@Schema(
    name = "Customer Details",
    description = "Schema to hold Customer Details information"
)
public class CustomerDetailsDto {
  @NotEmpty(message = "Name can not be a null or empty")
  @Size(min = 5, max = 30, message = "The length of the customer name should be between 5 and 30")
  @Schema(description = "Name of the customer", example = "Di Dan")
  private String name;

  @NotEmpty(message = "Email address can not be a null or empty")
  @Email(message = "Email address should be a valid value")
  @Schema(description = "Email address of the customer", example = "didan@gmail.com")
  private String email;

  @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
  @NotEmpty(message = "Mobile number can not be a null or empty")
  @Schema(description = "Mobile Number of the customer", example = "9345432123")
  private String mobile;

  @JsonProperty("accounts")
  @Schema(description = "Account details of the Customer")
  private AccountsDto accountsDto;

  @JsonProperty("cards")
  @Schema(description = "Card details of the Customer")
  private CardsDto cardsDto;

  @JsonProperty("loans")
  @Schema(description = "Loan details of the Customer")
  private LoansDto loansDto;

}
