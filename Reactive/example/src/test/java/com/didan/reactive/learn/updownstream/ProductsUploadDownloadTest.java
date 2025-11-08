package com.didan.reactive.learn.updownstream;

import com.didan.reactive.learn.updownstream.dto.ProductDto;
import com.didan.reactive.learn.updownstream.util.FileWriter;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Slf4j
public class ProductsUploadDownloadTest {

    private final ProductClient productClient = new ProductClient();

    @Test
    public void upload() {
        var flux = Flux.range(1, 1_000_000)
                       .map(i -> new ProductDto(null, "product-" + i, i));

        this.productClient.uploadProducts(flux)
                          .doOnNext(r -> log.info("received: {}", r))
                          .then()
                          .as(StepVerifier::create)
                          .expectComplete()
                          .verify();
    }

    @Test
    public void download() {
        this.productClient.downloadProducts()
                          .map(ProductDto::toString)
                          .as(flux -> FileWriter.create(flux, Path.of("products.txt")))
                          .as(StepVerifier::create)
                          .expectComplete()
                          .verify();
    }

}
