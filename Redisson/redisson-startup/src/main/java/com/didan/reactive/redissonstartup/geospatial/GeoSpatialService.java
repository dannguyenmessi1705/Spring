package com.didan.reactive.redissonstartup.geospatial;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.GeoUnit;
import org.redisson.api.RGeoReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.geo.GeoSearchArgs;
import org.redisson.api.geo.OptionalGeoSearch;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class GeoSpatialService {

  private final RedissonReactiveClient redissonReactiveClient;

  @PostConstruct
  public void geoSpatialI() {
    RGeoReactive<RestaurantDto> geo = redissonReactiveClient.getGeo("restaurants", new TypedJsonJacksonCodec(RestaurantDto.class)); // Sử dụng TypedJsonJacksonCodec để mã hóa/giải mã đối tượng RestaurantDto trong Redis Key "restaurants"
    // Thêm dữ liệu địa lý mẫu
    Mono<Void> mono = Flux.fromIterable(RestaurantUtils.getRestaurants()) // Lấy danh sách nhà hàng mẫu
        .flatMap(r -> geo.add(r.getLongitude(), r.getLatitude(), r)) // Thêm từng nhà hàng vào cấu trúc địa lý của Redisson (sử dụng kinh độ, vĩ độ và đối tượng nhà hàng)
        .doOnComplete(() -> log.info("GeoSpatial I executed")) // Ghi log khi hoàn thành việc thêm dữ liệu
        .then(); // Chuyển đổi sang Mono<Void> để biểu thị hoàn thành

    OptionalGeoSearch radius = GeoSearchArgs.from(-96.43243, 32.35553).radius(6, GeoUnit.MILES); // Tạo đối tượng tìm kiếm bán kính với tọa độ và bán kính cụ thể (3 km)
    // Thực hiện tìm kiếm các nhà hàng trong bán kính đã chỉ định
    Mono<Void> result = geo.search(radius)
        .doOnNext(r -> log.info("Restaurant found: {}", r)) // Ghi log thông tin nhà hàng tìm thấy
        .then(); // Kết thúc chuỗi phản ứng

    mono.concatWith(result).subscribe(); // Kết hợp hai Mono và đăng ký để thực thi
  }
}
