package com.didan.logquickwit.controller;

import com.didan.logquickwit.dto.ResponseDto;
import com.didan.logquickwit.service.FakeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FakeController {
    private final FakeService fakeService;

    @PostMapping("/fake")
    ResponseEntity<ResponseDto> fakeController(@RequestBody Object request) {
        log.info("FakeController called");
        ResponseDto responseDto = fakeService.fakeService(request);
        return ResponseEntity.ok(responseDto);
    }
}
