package com.didan.test_mokito.business;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

class MockBusiness {

	@Test
	void testFindMax_1() {
		DataService mockDataService = mock(DataService.class);
		when(mockDataService.getAllDatas()).thenReturn(new int[] {10, 20, 30}); 
		Business mockBusiness = new Business(mockDataService);
		int result = mockBusiness.findMax();
		int expected = 30;
		assertEquals(expected, result);
	}
	
	@Test
	void testFindMax_2() {
		DataService mockDataService = mock(DataService.class);
		when(mockDataService.getAllDatas()).thenReturn(new int[] { 20 });
		Business mockBusiness = new Business(mockDataService);
		int result = mockBusiness.findMax();
		int expected = 20;
		assertEquals(expected, result);
	}
}
