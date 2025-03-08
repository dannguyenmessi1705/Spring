package com.didan.vault.config;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class VaultProperties {
 private List<VaultConfig> vaultConfigs;
}
