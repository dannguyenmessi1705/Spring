package com.didan.learn_jpa_and_hibernate.course.jpa;

import org.springframework.stereotype.Repository;

import com.didan.learn_jpa_and_hibernate.course.CourseEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository // Đánh dấu đây là một bean của Spring, dùng để thực hiện các thao tác với
			// database
@Transactional // Đánh dấu các phương thức ở đây sẽ thực thi trong một transaction của JPA,
				// giúp cho
				// việc thao tác với database an toàn hơn
public class CourseJpa {
	@PersistenceContext // Inject EntityManager của JPA vào đây (tương tự như @Autowired)
	private EntityManager entityManager; // Dùng để thực hiện các thao tác với database (insert, update, delete, select)

	public void insert(CourseEntity course) {
		entityManager.merge(course); // merge() dùng để tạo, cập nhật 1 entity trong database, với tham số là entity
	}

	public void deleteId(long id) {
		CourseEntity course = entityManager.find(CourseEntity.class, id); // Tìm entity trong database với tham số đầu
																			// là class Entity và tham số thứ 2 là id
		entityManager.remove(course); // Xóa entity trong database với tham số là entity
	}

	public CourseEntity getCourse(long id) {
		return entityManager.find(CourseEntity.class, id); // Tìm entity trong database với tham số đầu là class Entity
															// và
															// tham số thứ 2 là id
	}
}
