package com.didan.vault.controller;

import com.didan.vault.config.SignatureConfig;
import com.didan.vault.service.GetKVVaultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vault")
@RequiredArgsConstructor
public class VaultController {
  private final GetKVVaultService getKVVaultService;
  @GetMapping()
  ResponseEntity<SignatureConfig> getSignatureBank(@RequestParam String bank) {
    return ResponseEntity.ok(getKVVaultService.returnSignatureBank(bank));
  }
}
