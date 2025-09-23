package com.didan.isosocketconnection.serversocket.controller;

import com.didan.isosocketconnection.serversocket.service.NettyServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/server")
@RequiredArgsConstructor
public class ServerController {

    private final NettyServerService nettyServerService;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getServerStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("running", nettyServerService.isRunning());
        status.put("info", nettyServerService.getServerInfo());
        status.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(status);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();

        if (nettyServerService.isRunning()) {
            health.put("status", "UP");
            health.put("description", "ISO Socket Server is running");
        } else {
            health.put("status", "DOWN");
            health.put("description", "ISO Socket Server is not running");
        }

        return ResponseEntity.ok(health);
    }
}
