package com.didan.cert;

import com.didan.cert.utils.GenerateKey;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CertApplication {

  public static void main(String[] args) throws Exception {
    GenerateKey.generate();
    SpringApplication.run(CertApplication.class, args);
  }

}
