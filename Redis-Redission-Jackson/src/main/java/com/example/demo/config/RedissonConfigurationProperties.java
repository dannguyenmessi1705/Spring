package com.example.demo.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@ConfigurationProperties("app.cache.redis")
@Data
public class RedissonConfigurationProperties {
  // Cấu hình Redis với Máy chủ là SingleNode
  private String address = "redis://redis-10569.c302.asia-northeast1-1.gce.redns.redis-cloud.com:10569"; // Địa chỉ máy chủ Redis dạng redis://host:port
  private int subscriptionConnectionMinimumIdleSize = 1; // Kích thước tối thiểu của pool kết nối trống cho các kênh đăng ký (pub/sub). Được sử dụng bởi các đối tượng RTopic, RPatternTopic, RLock, RSemaphore, RCountDownLatch, RClusteredLocalCachedMap, RClusteredLocalCachedMapCache, RLocalCachedMap, RLocalCachedMapCache và Hibernate Local Cached Region Factories.
  private int subscriptionConnectionPoolSize = 50; // Kích thước tối đa của pool kết nối cho các kênh đăng ký (pub/sub). Được sử dụng bởi các đối tượng RTopic, RPatternTopic, RLock, RSemaphore, RCountDownLatch, RClusteredLocalCachedMap, RClusteredLocalCachedMapCache, RLocalCachedMap, RLocalCachedMapCache và Hibernate Local Cached Region Factories.
  private int connectionMinimumIdleSize = 24; // Số lượng kết nối Redis đang rảnh tối thiểu.
  private int connectionPoolSize = 64; // Kích thước tối đa của pool kết nối Redis.
  private int dnsMonitoringInterval = 5000; // Khoảng thời gian giám sát thay đổi DNS. Ứng dụng phải đảm bảo TTL bộ nhớ cache JVM DNS đủ thấp để hỗ trợ điều này. Đặt -1 để vô hiệu hóa. Chế độ Proxy hỗ trợ nhiều kết nối IP cho một tên máy chủ duy nhất.
  private int idleConnectionTimeout = 10000; // Nếu kết nối được gom không được sử dụng trong khoảng thời gian chờ và số lượng kết nối hiện tại lớn hơn kích thước pool kết nối rảnh tối thiểu, thì nó sẽ bị đóng và loại bỏ khỏi pool. Giá trị tính bằng mili giây.
  private int connectTimeout = 10000; // Thời gian chờ kết nối với bất kỳ máy chủ Redis nào.
  private int timeout = 3000; // Thời gian chờ phản hồi từ máy chủ Redis. Bắt đầu đếm ngược khi lệnh Redis được gửi thành công. Giá trị tính bằng mili giây.
  private int retryAttempts = 3; // Số lần thử lại gửi lệnh Redis đến máy chủ Redis trước khi bỏ cuộc. Nếu lệnh Redis không thể được gửi đến máy chủ Redis sau retryAttempts, lỗi sẽ được ném. Nhưng nếu nó được gửi thành công thì thời gian chờ sẽ bắt đầu.
  private int retryInterval = 1500; // Khoảng thời gian sau đó một lần thử gửi lệnh Redis khác sẽ được thực hiện. Giá trị tính bằng mili giây.
  private int database = 0; // Chỉ số cơ sở dữ liệu được sử dụng cho kết nối Redis (Mặc định là 0).
  private String username = "default"; // Tên người dùng cho xác thực máy chủ Redis. Yêu cầu Redis 6.0+
  private String password = "lL72ALG8YqUPTQw8qghJQageck9xxfER"; // Mật khẩu cho xác thực máy chủ Redis.
  private int subscriptionsPerConnection = 5; // Giới hạn số lượng đăng ký trên mỗi kết nối đăng ký. Được sử dụng bởi các đối tượng RTopic, RPatternTopic, RLock, RSemaphore, RCountDownLatch, RClusteredLocalCachedMap, RClusteredLocalCachedMapCache, RLocalCachedMap, RLocalCachedMapCache và Hibernate Local Cached Region Factories.
  private String clientName = null; // Tên kết nối khách hàng

  // Cấu hình Redis với Máy chủ là Cluster Node
  private boolean checkSlotsCoverage = true; // Cho phép kiểm tra slot cụm trong quá trình khởi động Redisson.
  private String[] nodeAddresses = {"redis://10.207.56.44:8379", "redis://10.207.56.45:8379", "redis://10.207.56.46:8379", "redis://10.207.56.44:8479", "redis://10.207.56.45:8479", "redis://10.207.56.46:8479" }; // Thêm địa chỉ node Redis cluster hoặc địa chỉ đầu cuối Redis dạng host:port. Redisson tự động khám phá cấu trúc cụm. Sử dụng giao thức rediss:// để kết nối SSL.
  private int canInterval = 1000; // Khoảng thời gian quét trong mili giây. Áp dụng cho quét cấu trúc cụm Redis.
  private String readMode = "SLAVE"; // Đặt loại nút được sử dụng cho hoạt động đọc. Các giá trị có sẵn: SLAVE - Đọc từ các nút slave, sử dụng MASTER nếu không có SLAVES nào khả dụng, MASTER - Đọc từ nút master, MASTER_SLAVE - Đọc từ nút master và slave
  private String subscriptionMode = "SLAVE"; // Đặt loại nút được sử dụng cho hoạt động đăng ký. Các giá trị có sẵn: SLAVE - Đăng ký cho các nút slave, MASTER - Đăng ký cho nút master, loadBalancer
//  private int subscriptionConnectionMinimumIdleSize = 1; // Kích thước tối thiểu của pool kết nối trống cho các kênh đăng ký (pub/sub). Được sử dụng bởi các đối tượng RTopic, RPatternTopic, RLock, RSemaphore, RCountDownLatch, RClusteredLocalCachedMap, RClusteredLocalCachedMapCache, RLocalCachedMap, RLocalCachedMapCache và Hibernate Local Cached Region Factories.
//  private int subscriptionConnectionPoolSize = 50; // Kích thước tối đa của pool kết nối cho các kênh đăng ký (pub/sub). Được sử dụng bởi các đối tượng RTopic, RPatternTopic, RLock, RSemaphore, RCountDownLatch, RClusteredLocalCachedMap, RClusteredLocalCachedMapCache, RLocalCachedMap, RLocalCachedMapCache và Hibernate Local Cached Region Factories.
  private int slaveConnectionMinimumIdleSize = 24; // Số lượng kết nối rảnh tối thiểu cho mỗi nút slave Redis.
  private int slaveConnectionPoolSize = 64; // Kích thước tối đa của pool kết nối cho mỗi nút slave Redis.
  private int masterConnectionMinimumIdleSize = 24; // Số lượng kết nối rảnh tối thiểu cho mỗi nút master Redis.
  private int masterConnectionPoolSize = 64; // Kích thước tối đa của pool kết nối cho mỗi nút master Redis.
//  private int idleConnectionTimeout = 10000; // Nếu kết nối được gom không được sử dụng trong khoảng thời gian chờ và số lượng kết nối hiện tại lớn hơn kích thước pool kết nối rảnh tối thiểu, thì nó sẽ bị đóng và loại bỏ khỏi pool. Giá trị tính bằng mili giây.
//  private int connectTimeout = 10000; // Thời gian chờ kết nối với bất kỳ máy chủ Redis nào.
//  private int timeout = 3000; // Thời gian chờ phản hồi từ máy chủ Redis. Bắt đầu đếm ngược khi lệnh Redis được gửi thành công. Giá trị tính bằng mili giây.
//  private int retryAttempts = 3; // Số lần thử lại gửi lệnh Redis đến máy chủ Redis trước khi bỏ cuộc. Nếu lệnh Redis không thể được gửi đến máy chủ Redis sau retryAttempts, lỗi sẽ được ném. Nhưng nếu nó được gửi thành công thì thời gian chờ sẽ bắt đầu.
//  private int etryInterval = 1500; // Khoảng thời gian sau đó một lần thử gửi lệnh Redis khác sẽ được thực hiện. Giá trị tính bằng mili giây.
  private int failedSlaveReconnectionInterval = 3000; // Khoảng thời gian thử kết nối lại Redis Slave khi nó bị loại khỏi danh sách nội bộ của các máy chủ có sẵn. Trên mỗi sự kiện timeout, Redisson cố gắng kết nối với máy chủ Redis bị ngắt kết nối. Giá trị tính bằng mili giây.
//  private String username = "default"; // Tên người dùng cho xác thực máy chủ Redis. Yêu cầu Redis 6.0+
//  private String password = "lL72ALG8YqUPTQw8qghJQageck9xxfER"; // Mật khẩu cho xác thực máy chủ Redis.
//  private int subscriptionsPerConnection = 5; // Giới hạn số lượng đăng ký trên mỗi kết nối đăng ký. Được sử dụng bởi các đối tượng RTopic, RPatternTopic, RLock, RSemaphore, RCountDownLatch, RClusteredLocalCachedMap, RClusteredLocalCachedMapCache, RLocalCachedMap, RLocalCachedMapCache và Hibernate Local Cached Region Factories.
//  private String clientName = null; // Tên kết nối khách hàng.
  private int pingConnectionInterval = 30000; // Định nghĩa khoảng thời gian giữa các lệnh PING được gửi. Giá trị mặc định là 3000 mili giây. Cài đặt này cho phép phát hiện và kết nối lại các kết nối bị hỏng bằng lệnh PING. Đặt 0 để vô hiệu hóa.
  private boolean keepAlive = false; // Không bắt buộc kích hoạt TCP keepAlive cho kết nối.
  private boolean tcpNoDelay = true; // Cho phép TCP noDelay cho kết nối.
  private int scanInterval = 1000; // Khoảng thời gian giữa các lần quét các kết nối Redis. Được sử dụng để kiểm tra kết nối Redis có hoạt động hay không.


  // ttl
  private long ttl = 60000;
  private Map<String, Long> cacheExpirations = new HashMap<>(); // Mapping cacheNames với thời gian hết hạn sau khi ghi trong giây.
}
