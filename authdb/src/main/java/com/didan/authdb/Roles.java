package com.didan.authdb;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Roles {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@Column(name = "role", nullable = false)
	private String role;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private Users user;

	public Roles(String role) {
		this.role = role;
	}

	public Roles() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setUser(Users user) {
		this.user = user;
	}

}
