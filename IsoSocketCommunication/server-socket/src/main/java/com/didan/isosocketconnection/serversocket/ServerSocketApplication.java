package com.didan.isosocketconnection.serversocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ServerSocketApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerSocketApplication.class, args);
		log.info("ISO Socket Server Application started successfully");
	}
}
