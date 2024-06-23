package com.didan.learn_jpa_and_hibernate.course.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository // Đánh dấu đây là một bean của Spring, dùng để thực hiện các thao tác với
			// database
public class CourseJdbc {
	@Autowired
	private JdbcTemplate jdbcTemplate; // Dùng để thực hiện các thao tác với database (insert, update, delete, select)

	public static String INSERT_SQL = """
			INSERT INTO courses (id, name, author)
			VALUES
			(1, 'Spring Boot', 'didan');
			"""; // Câu lệnh SQL để thêm một bản ghi vào bảng courses, sử dụng cú pháp mới của
					// Java 13 3 dấu nháy kép `"""` để viết chuỗi trên nhiều dòng
	// String trong câu lệnh SQL phải được viết trong dấu nháy đơn `'` thay vì dấu
	// nháy kép `"`

	public void insert() {
		jdbcTemplate.update(INSERT_SQL); // Thực thi câu lệnh SQL INSERT_SQL bằng jdbcTemplate
	}
}
