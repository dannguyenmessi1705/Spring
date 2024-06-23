package com.didan.learn_jpa_and_hibernate.course;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "courses") // Đánh dấu đây là một entity của JPA, tương ứng với bảng courses trong
							// database, nếu không đặt tên thì mặc định sẽ lấy tên class
public class CourseEntity {
	@Id
	private long id;

	@Column(name = "name") // Đánh dấu đây là một cột trong bảng courses, tương ứng với cột name trong
							// database
	private String name;

	@Column(name = "author") // Đánh dấu đây là một cột trong bảng courses, tương ứng với cột author trong
								// database
	private String author;

	public CourseEntity() {
		super();
	}

	public CourseEntity(long id, String name, String author) {
		super();
		this.id = id;
		this.name = name;
		this.author = author;
	}

	// Cần có getter và setter cho các trường trong entity để JPA có thể mapping dữ
	// liệu từ database vào entity và ngược lại

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public String toString() {
		return "CourseEntity [id=" + id + ", name=" + name + ", author=" + author + "]";
	}

}
