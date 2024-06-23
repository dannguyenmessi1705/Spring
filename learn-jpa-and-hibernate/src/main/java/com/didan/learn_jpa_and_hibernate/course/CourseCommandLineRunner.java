package com.didan.learn_jpa_and_hibernate.course;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.didan.learn_jpa_and_hibernate.course.jdbc.CourseJdbc;
import com.didan.learn_jpa_and_hibernate.course.jpa.CourseJpa;
import com.didan.learn_jpa_and_hibernate.course.springdatajpa.CourseSpringDataJpa;

@Component // Đánh dấu đây là một bean của Spring, dùng để thực thi một số thao tác khi ứng
			// dụng chạy
public class CourseCommandLineRunner implements CommandLineRunner { // CommandLineRunner là một interface của Spring
																	// Boot, dùng để thực thi một số thao tác khi
																	// ứng dụng chạy

	@Autowired
	private CourseJdbc courseJdbc; // Inject bean CourseJdbc vào đây

	@Autowired
	private CourseJpa courseJpa;
	
	@Autowired
	private CourseSpringDataJpa springDataJpa;

	@Override
	public void run(String... args) throws Exception {

		// JDBC
//		courseJdbc.insert(); // Gọi phương thức insert() của bean CourseJdbc khi ứng dụng chạy
//		courseJdbc.insertTemplate(new Course(2, "NextJs", "didan"));
//		courseJdbc.insertTemplate(new Course(3, "Python", "didan"));
//		courseJdbc.deleteId(1);
//		
//		System.out.println(courseJdbc.getCourse(2));
		
		// JPA
//		courseJpa.insert(new CourseEntity(1, "Spring Boot", "didan"));
//		courseJpa.insert(new CourseEntity(2, "NextJs", "didan"));
//		courseJpa.insert(new CourseEntity(3, "Python", "didan"));
//		courseJpa.deleteId(1);
//		
//		System.out.println(courseJpa.getCourse(2));
		
		// Spring Data JPA
		springDataJpa.save(new CourseEntity(1, "Spring Boot", "didan"));
		springDataJpa.save(new CourseEntity(2, "NextJs", "didan"));
		springDataJpa.save(new CourseEntity(3, "Python", "didan"));
		springDataJpa.deleteById(1l);
		
		System.out.println(springDataJpa.findFirstByName("Python"));
		System.out.println(springDataJpa.findById(2l));
	}

}
