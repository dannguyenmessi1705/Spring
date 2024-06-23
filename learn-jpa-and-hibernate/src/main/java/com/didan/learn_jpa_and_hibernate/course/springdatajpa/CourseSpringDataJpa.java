package com.didan.learn_jpa_and_hibernate.course.springdatajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.didan.learn_jpa_and_hibernate.course.CourseEntity;

@Repository // Đánh dấu đây là một bean của Spring, dùng để thực hiện các thao tác với
			// database
public interface CourseSpringDataJpa extends JpaRepository<CourseEntity, Long> {
	CourseEntity findFirstByName(String name);
}

