package com.didan.spring_boot_restapi.exception;

import java.time.LocalDateTime;

public class ErrorDetails { // Tạo 1 class ErrorDetails dùng để tùy chỉnh thông tin lỗi trả về, thay vi trả về thông tin lỗi mặc định của Spring Boot
	private LocalDateTime timestamp;
	private String message;
	private String details;

	public ErrorDetails(LocalDateTime timestamp, String message, String details) {
		super();
		this.timestamp = timestamp;
		this.message = message;
		this.details = details;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public String getMessage() {
		return message;
	}

	public String getDetails() {
		return details;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setDetails(String details) {
		this.details = details;
	}
}
