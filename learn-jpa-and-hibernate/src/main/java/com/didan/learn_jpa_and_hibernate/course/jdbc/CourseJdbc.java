package com.didan.learn_jpa_and_hibernate.course.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.didan.learn_jpa_and_hibernate.course.Course;

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

	// Ngoài ra với các tham số động, chúng ta có thể sử dụng cú pháp `?` để thay
	// thế
	public static String INSERT_TEMPLATE = """
			INSERT INTO courses (id, name, author)
			VALUES
			(?, ?, ?);
			""";

	public static String DELETE_TEMPLATE = """
			DELETE FROM courses WHERE id = ?;
			""";

	public void insert() {
		jdbcTemplate.update(INSERT_SQL); // Thực thi câu lệnh SQL INSERT_SQL bằng jdbcTemplate
	}

	public void insertTemplate(Course course) {
		jdbcTemplate.update(INSERT_TEMPLATE, course.getId(), course.getName(), course.getAuthor()); // Thực thi câu lệnh
																									// SQL
																									// INSERT_TEMPLATE
																									// bằng jdbcTemplate
																									// với các tham số
																									// động
	}

	public void deleteId(long id) {
		jdbcTemplate.update(DELETE_TEMPLATE, id); // Thực thi câu lệnh SQL DELETE_TEMPLATE bằng jdbcTemplate với các
													// tham số động
	}
}
