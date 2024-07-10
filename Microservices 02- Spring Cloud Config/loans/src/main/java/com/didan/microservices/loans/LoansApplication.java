package com.didan.microservices.loans;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl") // Sử dụng JPA Auditing và trỏ đến Bean auditAwareImpl để lấy thông tin người tạo hoặc cập nhật dữ liệu
@OpenAPIDefinition(
	info = @Info(
		title = "Microservices Loans API documents",
		description = "Microservices Loans API documents",
		version = "v1",
		contact = @Contact(
			name = "Nguyen Di Dan",
			url = "https://my.didan.id.vn",
			email = "didannguyen17@gmail.com"
		),
		license = @License(
			name = "Apache 2.0",
			url = "https://my.didan.id.vn"
		),
		summary = "Microservices Loans API documents"
	),
	servers = {
		@Server(
			description = "Localhost",
			url = "http://localhost:8090"
		),
		@Server(
			description = "Deploy",
			url = "https://deploy.com"
		)
	}
)
@SecuritySchemes({
	@SecurityScheme(
		name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		bearerFormat = "JWT",
		scheme = "Bearer"
	)
})
public class LoansApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoansApplication.class, args);
	}

}
