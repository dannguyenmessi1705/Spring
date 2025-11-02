package com.didan.reactive.learn.r2dbc;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "package=r2dbc" // Chỉ định gói để cấu hình ứng dụng cho các bài kiểm tra liên quan đến R2DBC
})
public abstract class AbstractTest {

}
