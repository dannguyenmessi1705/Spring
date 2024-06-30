package junit.didan.calculate;

import static org.junit.jupiter.api.Assertions.*; // Import các phương thức static từ thư viện org.junit.jupiter.api.Assertions

import org.junit.jupiter.api.Test; // Import phương thức Test từ thư viện org.junit.jupiter.api

class CalculateTest {

	@Test // Đánh dấu phương thức testAddCalculate() là một test case
	void testAddCalculate() {
		Calculate calculate = new Calculate();
		int actual = calculate.add(1, 2, 3, 4);
		int expected = 10;
		
		assertEquals(expected, actual); // Phuong thuc assertEquals() so sanh ket qua mong doi voi ket qua thuc te, nếu khác nhau thì hiển thị thông báo lỗi
		
//		fail("Not yet implemented"); // Phuong thuc fail() luôn fail test case và hiển thị thông báo lỗi
	}

}
