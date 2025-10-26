package com.didan.logquickwit.service;

import com.didan.logquickwit.dto.ResponseDto;
import com.didan.logquickwit.entity.LogEntity;
import com.didan.logquickwit.repository.LogRepository;
import com.didan.logquickwit.utils.CommonUtils;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FakeService {

    private final LogRepository logRepository;
    Faker faker = new Faker();

    public ResponseDto fakeService(Object request) {
        log.info("FakeService called");
        LogEntity logEntity = new LogEntity();
        logEntity.setLogRequest(CommonUtils.GSON.toJson(request));
        String fakeName = faker.name().firstName() + " " + faker.name().lastName();
        String fakeAddress = faker.address().fullAddress();
        String avatar = faker.avatar().image();
        ResponseDto responseDto = new ResponseDto(fakeName, fakeAddress, avatar);
        logEntity.setLogResponse(CommonUtils.GSON.toJson(responseDto));
        logRepository.save(logEntity);
        log.info("Saved logEntity {}", logEntity);
        return responseDto;
    }
}
