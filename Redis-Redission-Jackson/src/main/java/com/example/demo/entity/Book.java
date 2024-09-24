package com.example.demo.entity;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Book {
  private String title;
  private int price;
  private int publishedYear;
  private List<Chap> chaps;
}
