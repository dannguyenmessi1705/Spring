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
		try {
			Thread.sleep(30); // Giả lập thời gian chạy của method là 30ms
		} catch (InterruptedException e) {
			e.printStackTrace(); // Xử lý ngoại lệ
		}
		return Arrays.stream(arr).max().orElse(0); // Tìm max của mảng hoặc trả về 0 nếu rỗng
	}
}
