package com.didan.learn_spring_aop.repository;

import org.springframework.stereotype.Repository;

@Repository
public class Data {
	public int[] retrieveArray() {
		return new int[] {44, 55, 88, 22, 11};
	}
}
