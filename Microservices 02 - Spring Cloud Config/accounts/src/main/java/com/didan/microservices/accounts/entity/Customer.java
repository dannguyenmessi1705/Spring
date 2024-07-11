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

@Entity(name = "customer")
@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
public class Customer extends BasicClass{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native") // AUTO: tự động tăng, native: sử dụng cơ chế của DB
	@Column(name = "customer_id")
	private Long customerId;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "email")
	private String email;
	
	
	@Column(name = "mobile_number")
	private String mobile;

}
