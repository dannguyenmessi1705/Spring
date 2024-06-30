package junit.didan.calculate;

import static org.junit.jupiter.api.Assertions.*; // Import các phương thức static từ thư viện org.junit.jupiter.api.Assertions

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test; // Import phương thức Test từ thư viện org.junit.jupiter.api

class CalculateTest {
	
	@BeforeAll // Đánh dấu phương thức setUpAll() là một phương thức thiết lập trước tất cả test case
	static void setUpAll() { // Bắt buộc phải là phương thức static
		System.out.println("Before All test case");
	}
	
	@BeforeEach // Đánh dấu phương thức setUp() là một phương thức thiết lập trước mỗi test case
	void setUp() throws Exception {
		System.out.println("Before Each test case");
	}

	@Test // Đánh dấu phương thức testAddCalculate1() là một test case
	void testAddCalculate1() {
		Calculate calculate = new Calculate();
		int actual = calculate.add(1, 2, 3, 4);
		int expected = 10;
		
		assertEquals(expected, actual); // Phuong thuc assertEquals() so sanh ket qua mong doi voi ket qua thuc te, nếu khác nhau thì hiển thị thông báo lỗi
		
//		fail("Not yet implemented"); // Phuong thuc fail() luôn fail test case và hiển thị thông báo lỗi
	}
	
	@Test // Đánh dấu phương thức testAddCalculate2() là một test case
	void testAddCalculate2() {
		Calculate calculate = new Calculate();
		int actual = calculate.add(7, 8, 9, 10);
		int expected = 34;
		
		assertEquals(expected, actual); // Phuong thuc assertEquals() so sanh ket qua mong doi voi ket qua thuc te, nếu khác nhau thì hiển thị thông báo lỗi
		
//		fail("Not yet implemented"); // Phuong thuc fail() luôn fail test case và hiển thị thông báo lỗi
	}
	
	@AfterEach // Đánh dấu phương thức tearDown() là một phương thức thiết lập sau mỗi test case
	void tearDown() throws Exception {
		System.out.println("After Each test case");
	}

	@AfterAll // Đánh dấu phương thức tearDownAll() là một phương thức thiết lập sau tất cả test case
	static void tearDownAll() { // Bắt buộc phải là phương thức static
		System.out.println("After All test case");
	}
}
