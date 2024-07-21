package com.didan.microservices.cards.dto;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter @Setter
@ConfigurationProperties(prefix = "cards")
public class CardsContactInfoDto{
  private String message;
  private Map<String, String> contactDetails;
  private List<String> onCallSupport;
}
