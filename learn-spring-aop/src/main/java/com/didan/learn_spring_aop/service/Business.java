package com.didan.learn_spring_aop.service;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.didan.learn_spring_aop.repository.Data;

@Service
public class Business {
	private final Data data;
	
	public Business(Data data) {
		this.data = data;
	}
	
	public int getMax() {
		int[] arr = data.retrieveArray();
		return Arrays.stream(arr).max().orElse(0); // Tìm max của mảng hoặc trả về 0 nếu rỗng
	}
}
