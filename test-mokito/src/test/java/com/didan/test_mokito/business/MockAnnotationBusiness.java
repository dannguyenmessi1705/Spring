package com.didan.test_mokito.business;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
 
@ExtendWith(MockitoExtension.class) //  Sử dụng MockitoExtension.class để sử dụng @Mock, @InjectMocks
class MockAnnotationBusiness {

	@Mock // Tạo ra một đối tượng giả mạo mockDataService từ DataService.
	private DataService mockDataService;
	
	@InjectMocks // Tạo ra một đối tượng giả mạo mockBusiness từ Business và inject mockDataService vào mockBusiness.
	// Khi mockBusiness được tạo ra, mockDataService sẽ được inject vào mockBusiness.
	// == Business mockBusiness = new Business(mockDataService);
	private Business mockBusiness;
	
	@Test
	void test() {
		when(mockDataService.getAllDatas()).thenReturn(new int[] {10, 20, 30});
		int result = mockBusiness.findMax();
		int expected = 30;
		assertEquals(expected, result);
	}
	
	@Test 
	void test_2() {
		when(mockDataService.getAllDatas()).thenReturn(new int[] {});
        int result = mockBusiness.findMax();
        int expected = Integer.MIN_VALUE;
        assertEquals(expected, result);
	}

}
