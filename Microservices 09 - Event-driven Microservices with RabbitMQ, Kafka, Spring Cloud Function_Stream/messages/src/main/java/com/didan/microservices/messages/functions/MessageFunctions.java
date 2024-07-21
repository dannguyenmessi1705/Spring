package com.didan.microservices.messages.functions;

import com.didan.microservices.messages.dto.AccountsMsgDto;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // Đánh dấu lớp này là một lớp cấu hình Spring, tạo ra các bean
public class MessageFunctions {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  @Bean // Đánh dấu phương thức này là một bean
  public Function<AccountsMsgDto, AccountsMsgDto> email() { // Tạo ra một bean kiểu Function với đầu vào và đầu ra là AccountsMsgDto, endpoint là /email
    return accountsMsgDto -> { // accountsMsgDto là đầu vào của hàm
      logger.info("Sending email to: {}", accountsMsgDto.toString());
      return accountsMsgDto; // Trả về đầu ra của hàm
    }; // Phải được viết bằng Lambda Expression (->)
  }

  @Bean
  public Function<AccountsMsgDto, Long> sms() { // Tạo ra một bean kiểu Function với đầu vào là AccountsMsgDto và đầu ra là Long, endpoint là /sms
    return accountsMsgDto -> { // accountsMsgDto là đầu vào của hàm
      logger.info("Sending SMS to: {}", accountsMsgDto.toString());
      return accountsMsgDto.accountNumber(); // Trả về đầu ra của hàm
    }; // Phải được viết bằng Lambda Expression (->)
  }

  @Bean
  public Supplier<String> name() { // Tạo ra một bean kiểu Supplier với đầu ra là String, endpoint là /name
    return () -> { // Đầu vào của hàm là rỗng
      logger.info("Getting name example");
      return "John Doe"; // Trả về đầu ra của hàm
    }; // Phải được viết bằng Lambda Expression (->)
  }

  @Bean
  public Consumer<String> print() { // Tạo ra một bean kiểu Consumer với đầu vào là String, endpoint là /print
    return s -> logger.info("Printing: {}", s); // s là đầu vào của hàm, và trong Lambda Expression (->) không có return
  }
}
