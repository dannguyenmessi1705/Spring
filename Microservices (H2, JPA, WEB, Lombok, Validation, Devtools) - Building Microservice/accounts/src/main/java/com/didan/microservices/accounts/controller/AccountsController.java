package com.didan.microservices.accounts.controller;

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

import com.didan.microservices.accounts.constant.AccountsConstant;
import com.didan.microservices.accounts.dto.CustomerDto;
import com.didan.microservices.accounts.dto.ResponseDto;
import com.didan.microservices.accounts.service.IAccountsService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Validated
public class AccountsController {
	private final IAccountsService accountsService;

	@PostMapping("/create")
	public ResponseEntity<? super CustomerDto> createAccount(@Valid @RequestBody CustomerDto customerDto) {
		accountsService.createAccounts(customerDto);
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(new ResponseDto(AccountsConstant.STATUS_201, AccountsConstant.MESSAGE_201));
	}
	
	@GetMapping("/fetch")
	public ResponseEntity<? super CustomerDto> fetch(
			@RequestParam 
		    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
			String mobile) {
		CustomerDto customerDto = accountsService.fetch(mobile);
		return ResponseEntity.status(HttpStatus.OK)
				.body(customerDto);
	}
	
	@PatchMapping("/update")
	public ResponseEntity<?> update(@Valid @RequestBody CustomerDto customerDto) {
		boolean isUpdated = accountsService.update(customerDto);
		if (isUpdated) {
			return ResponseEntity.status(HttpStatus.OK)
					.body(new ResponseDto(AccountsConstant.STATUS_200, AccountsConstant.MESSAGE_200));
		} else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseDto(AccountsConstant.STATUS_500, AccountsConstant.MESSAGE_500));
	}
	
	@DeleteMapping("/delete")
	public ResponseEntity<?> delete(
			@RequestParam 
		    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
			String mobile) {
		boolean isDeleted = accountsService.delete(mobile);
		if (isDeleted) {
			return ResponseEntity.status(HttpStatus.OK)
					.body(new ResponseDto(AccountsConstant.STATUS_200, AccountsConstant.MESSAGE_200));
		} else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ResponseDto(AccountsConstant.STATUS_500, AccountsConstant.MESSAGE_500));
	}
}
