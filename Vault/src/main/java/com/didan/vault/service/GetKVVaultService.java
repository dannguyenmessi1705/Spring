package com.didan.vault.service;

import com.didan.vault.config.SignatureConfig;
import com.didan.vault.config.VaultConfig;
import com.didan.vault.config.VaultData;
import com.didan.vault.config.VaultProperties;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.VaultResponseSupport;

@Slf4j
@Getter
@Setter
@Component
@RequiredArgsConstructor
public class GetKVVaultService {
  @Value("${spring.cloud.vault.host}")
  private String host;

  @Value("${spring.cloud.vault.port}")
  private String port;

  @Value("${spring.cloud.vault.scheme}")
  private String schema;

  private final VaultProperties vaultProperties;
  private final VaultOperations vaultOperations;
  private List<SignatureConfig> signatureConfigs;

  @PostConstruct
  public void setSignatureConfigs() {
    log.info("config: {}", this.vaultProperties.getVaultConfigs());
    this.signatureConfigs = this.vaultProperties
        .getVaultConfigs()
        .stream()
        .map(this::getConfig)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private SignatureConfig getConfig(VaultConfig vaultConfig) {
    SignatureConfig config = getResponseFromVault(vaultConfig.getVaultKeyPath());
    if (config == null) {
      return null;
    }
    config.setPartnerCode(vaultConfig.getPartnerCode());
    return config;
  }

  private SignatureConfig getResponseFromVault(String vaultKeyPath) {
    String endpoint = String.format("%s://%s:%s%s", this.schema, this.host, this.port, vaultKeyPath);
    log.info("Get response from vault: {}", endpoint);
    VaultResponseSupport<VaultData> response = vaultOperations.read(endpoint, VaultData.class);
    log.info("Vault response: {} from key url: {}", response, endpoint);
    if (response == null || response.getData() == null) {
      log.warn("Vault response is null: {}", endpoint);
      return null;
    }
    return response.getData().getData();
  }

  public SignatureConfig returnSignatureBank(String bank) {
    return signatureConfigs.stream().filter(
        s -> s.getPartnerCode().equalsIgnoreCase(bank)).findFirst().orElse(null);
  }
}
