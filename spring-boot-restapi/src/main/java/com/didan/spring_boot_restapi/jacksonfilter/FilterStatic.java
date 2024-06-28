package com.didan.spring_boot_restapi.jacksonfilter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FilterStatic {
	
	@JsonProperty("user_name")
	private String userName;
	
	@JsonProperty("age")
	private int age;
	
	@JsonIgnore
	private String password;

	public FilterStatic() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FilterStatic(String userName, int age, String password) {
		super();
		this.userName = userName;
		this.age = age;
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "FilterStatic [userName=" + userName + ", age=" + age + ", password=" + password + "]";
	}

}
