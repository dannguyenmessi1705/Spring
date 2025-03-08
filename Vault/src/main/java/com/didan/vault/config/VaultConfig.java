package com.didan.vault.config;

import lombok.Data;

@Data
public class VaultConfig {
    private String partnerCode;
    private String vaultKeyPath;
    private String privateKeyName;
    private String partnerPublicKeyName;
}
