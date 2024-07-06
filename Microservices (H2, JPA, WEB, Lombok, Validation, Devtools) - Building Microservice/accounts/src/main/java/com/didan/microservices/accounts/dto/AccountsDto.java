package com.didan.microservices.accounts.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data // lombok annotation to generate getters and setters
public class AccountsDto {
    @NotEmpty(message = "AccountNumber can not be a null or empty")
    @Pattern(regexp="(^$|[0-9]{7})",message = "AccountNumber must be 7 digits")
	private int accountNumber;
	
    @NotEmpty(message = "AccountType can not be a null or empty")
	private String accountType;
	
    @NotEmpty(message = "BranchAddress can not be a null or empty")
	private String branchAddress;
}
