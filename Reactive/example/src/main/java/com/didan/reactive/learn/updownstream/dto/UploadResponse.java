package com.didan.reactive.learn.updownstream.dto;

import java.util.UUID;

public record UploadResponse(UUID confirmationId, Long productsCount) {

}
