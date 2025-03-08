package com.didan.vault.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SignatureConfig {
  private String partnerCode;

  @JsonProperty("dan-private-key")
  private String danPrivateKey;

  @JsonProperty("partner-public-key")
  private String partnerPublicKey;
}
