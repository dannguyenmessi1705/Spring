package com.didan.learn_jpa_and_hibernate.course.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component // Đánh dấu đây là một bean của Spring, dùng để thực thi một số thao tác khi ứng
			// dụng chạy
public class CourseJdbcCommandLineRunner implements CommandLineRunner { // CommandLineRunner là một interface của Spring
																		// Boot, dùng để thực thi một số thao tác khi
																		// ứng dụng chạy

	@Autowired
	private CourseJdbc courseJdbc; // Inject bean CourseJdbc vào đây

	@Override
	public void run(String... args) throws Exception {
		courseJdbc.insert(); // Gọi phương thức insert() của bean CourseJdbc khi ứng dụng chạy
	}

}
