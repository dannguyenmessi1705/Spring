package com.didan.learn_spring_boot;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "didan") // Lấy dữ liệu từ file application.properties với prefix là didan
@Component // Đánh dấu đây là một Bean
public class ConfigProp {
	private String url; // Gán giá trị "didan.url" từ file application.properties vào biến url
	private String username; // Gán giá trị "didan.username" từ file application.properties vào biến username
	private String key; // Gán giá trị "didan.key" từ file application.properties vào biến key

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
