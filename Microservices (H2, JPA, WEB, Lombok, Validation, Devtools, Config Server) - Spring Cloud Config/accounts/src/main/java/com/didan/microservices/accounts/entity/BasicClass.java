package com.didan.microservices.accounts.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@MappedSuperclass // Đánh dấu đây là Entity cha của các Entity khác sẽ kế thừa nó với các thuộc tính cơ bản
@Getter @Setter @ToString 
@EntityListeners(AuditingEntityListener.class) // Sử dụng AuditingEntityListener để tự động set giá trị cho các trường createdAt, createdBy, updatedAt, updatedBy
public class BasicClass {
	@CreatedDate // Đánh dấu trường này sẽ tự động set giá trị cho nó khi insert dữ liệu
	@Column(name = "created_at", updatable = false) // updatable = false: không cho phép update trường này chỉ cho phép insert, đọc
	private LocalDateTime createdAt;
	
	@CreatedBy // Đánh dấu trường này sẽ tự động set giá trị cho nó khi insert dữ liệu
	@Column(name = "created_by", updatable = false) // updatable = false: không cho phép update trường này chỉ cho phép insert, đọc
	private String createdBy;
	
	@LastModifiedDate // Đánh dấu trường này sẽ tự động set giá trị cho nó khi update dữ liệu
	@Column(name = "updated_at", insertable = false) // insertable = false: không cho phép insert trường này chỉ cho phép update, đọc
	private LocalDateTime updatedAt;
	
	@LastModifiedBy // Đánh dấu trường này sẽ tự động set giá trị cho nó khi update dữ liệu
	@Column(name = "updated_by", insertable = false) // insertable = false: không cho phép insert trường này chỉ cho phép update, đọc
	private String updatedBy;
}
