package com.didan.microservices.accounts.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "accounts")
@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
public class Accounts extends BasicClass{

	@Column(name = "customer_id")
	private Long customerId;
	
	@Id
	@Column(name = "account_number")
	private int accountNumber;
	
	@Column(name = "account_type")
	private String accountType;
	
	
	@Column(name = "branch_address")
	private String branchAddress;
}
