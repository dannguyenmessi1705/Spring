package com.didan.migrateflyway_spring.repository;

import com.didan.migrateflyway_spring.entity.Books;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Books, String> {

}
