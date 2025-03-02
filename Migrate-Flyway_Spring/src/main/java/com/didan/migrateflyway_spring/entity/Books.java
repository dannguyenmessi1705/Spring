package com.didan.migrateflyway_spring.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "books")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Books {
  @Id
  private String id;

  @Column(name = "book_name")
  private String bookName;

  @Column(name = "author")
  private String author;
}
