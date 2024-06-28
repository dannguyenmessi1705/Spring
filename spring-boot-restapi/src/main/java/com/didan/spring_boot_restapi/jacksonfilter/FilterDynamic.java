package com.didan.spring_boot_restapi.jacksonfilter;

import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("FilterDynamic")
public class FilterDynamic {
	private String userName;
	private int age;
	private String password;
	
	public FilterDynamic() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public FilterDynamic(String userName, int age, String password) {
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
	
}
