package helloworld;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

record Person(String name, int age, Address address) {
};

record Address(String firstLine, String city) {
};

// @Configuarion là một annotation dùng để đánh dấu một class là một
// configuration class, nó sẽ được Spring container sử dụng để cấu hình và quản
// lý các bean.
@Configuration
public class HelloWorld {

	// @Bean là một annotation dùng để đánh dấu một method trả về một bean, Spring
	// container sẽ quản lý và cung cấp bean này.
	@Bean
	public String name() {
		return "Dan";
	}

	@Bean
	public int age() {
		return 21;
	}

	@Bean
	public Person person() {
		return new Person("Zidane", 54, new Address("Muong La", "Son La"));
	}

	// Bean parameters
	// Có thể truyền vào các bean khác thông qua tham số của method, nếu các bean
	// này đã được khai báo trong cùng một configuration class.
	@Bean
	public Person person2(String name, int age, Address address3) { // Bean name, Bean age, Bean address3
		return new Person(name, age, address3);
	}

	@Bean(name = "address2") // get Bean phải dùng tên này thay vì lấy tên phương thức
	public Address address() {
		return new Address("Nguyen Trai", "Ha Noi");
	}

	@Bean(name = "address3")
	public Address address3() {
		return new Address("An Thi", "Hung Yen");
	}
}
