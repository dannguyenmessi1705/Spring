package com.didan.reactive.redissonstartup.geospatial;

import jakarta.annotation.PostConstruct;
import java.util.function.Function;
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

@Component
@Slf4j
@RequiredArgsConstructor
public class GeoSpatialService {

  private final RedissonReactiveClient redissonReactiveClient;

  @PostConstruct
  public void geoSpatialI() {
    RGeoReactive<RestaurantDto> geo = redissonReactiveClient.getGeo("restaurants", new TypedJsonJacksonCodec(RestaurantDto.class)); // Sử dụng TypedJsonJacksonCodec để mã hóa/giải mã đối tượng RestaurantDto trong Redis Key "restaurants"
    // Thêm dữ liệu địa lý mẫu
    Flux.fromIterable(RestaurantUtils.getRestaurants()) // Lấy danh sách nhà hàng mẫu
        .flatMap(r -> geo.add(r.getLongitude(), r.getLatitude(), r)) // Thêm từng nhà hàng vào cấu trúc địa lý của Redisson (sử dụng kinh độ, vĩ độ và đối tượng nhà hàng)
        .doOnComplete(() -> log.info("GeoSpatial I executed")) // Ghi log khi hoàn thành việc thêm dữ liệu
        .subscribe(); // Thực thi chuỗi các thao tác (Trong Reactive (phản ứng) cần có sự kiện subscribe để bắt đầu thực thi)

    OptionalGeoSearch radius = GeoSearchArgs.from(-96.80359, 32.78136).radius(3, GeoUnit.MILES); // Tạo đối tượng tìm kiếm bán kính với tọa độ và bán kính cụ thể (3 km)
    // Thực hiện tìm kiếm các nhà hàng trong bán kính đã chỉ định
    geo.search(radius) // Thực hiện tìm kiếm trong cấu trúc địa lý với điều kiện bán kính đã tạo
        .flatMapIterable(Function.identity()) // Chuyển đổi kết quả tìm kiếm thành luồng các nhà hàng
        .doOnNext(r -> log.info("Restaurant found: {}", r)) // Ghi log thông tin nhà hàng tìm thấy
        .subscribe(); // Thực thi chuỗi các thao tác tìm kiếm
  }
}
