package com.didan.test_mokito.business;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class StubBusiness {
	
	@Test
	public void testFinMax_1() {
		StubDataService stubDataService = new StubDataService();
		Business business = new Business(stubDataService);
		int result = business.findMax();
		int expected = 30;
		assertEquals(expected, result);
	}
	
	@Test
	public void testFinMax_2() {
		StubDataService1 stubDataService = new StubDataService1();
		Business business = new Business(stubDataService);
		int result = business.findMax();
		int expected = 20;
		assertEquals(expected, result);
	}
	
}

class StubDataService implements DataService {
	@Override
	public int[] getAllDatas() {
		return new int[] {10, 20, 30};
	}
}

class StubDataService1 implements DataService {
	@Override
	public int[] getAllDatas() {
		return new int[] { 20 };
	}
}