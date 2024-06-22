package com.didan.learn_spring_boot;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseControllers {
	@RequestMapping("/courses")
	public List<Course> getAllCourses() {
		return Arrays.asList(new Course(1, "SpringBoot", "didan"), new Course(2, "NextJS", "didan"));
	}
}
