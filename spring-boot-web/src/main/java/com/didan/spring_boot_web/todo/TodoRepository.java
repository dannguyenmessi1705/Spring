package com.didan.spring_boot_web.todo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface TodoRepository extends JpaRepository<Todo, Integer>{
	
	List<Todo> findByUsername(String username); // Tìm kiếm danh sách Todo theo username
	
	Todo findFirstById(int id); // Tìm kiếm Todo theo id

}
