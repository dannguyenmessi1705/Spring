package com.didan.microservices.accounts.functions;

import com.didan.microservices.accounts.service.IAccountsService;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountsFunction {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  @Bean
  public Consumer<Long> updateCommunication(IAccountsService accountsService) {
    // Consumer là một functional interface, nó có một phương thức accept() nhận vào một tham số Long và không trả về giá trị
    return accountNumber -> { // accountNumber là tham số đầu vào của hàm
      logger.info("Updating Communication status for the account number: {}",
          accountNumber.toString());
      accountsService.updateCommunicationStatus(accountNumber); // Gọi phương thức updateCommunicationStatus() của accountsService để thực hiện logic code
    };
  }
}
