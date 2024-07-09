# Spring Cloud Config
## 1. Giới thiệu
`Spring Cloud Config` là một dịch vụ cung cấp cấu hình cho các ứng dụng mà không cần phải cấu hình trực tiếp trong mã nguồn. `Spring Cloud Config` giúp chúng ta quản lý cấu hình ứng dụng một cách dễ dàng và linh hoạt hơn.

`Spring Cloud Config` cung cấp một số tính năng như:
- **Tích hợp với nhiều loại cơ sở dữ liệu cấu hình**: `Spring Cloud Config` hỗ trợ lưu trữ cấu hình trong nhiều loại cơ sở dữ liệu khác nhau như Git, Subversion, Vault, JDBC, Redis, MongoDB, v.v.
- **Tích hợp với nhiều loại ứng dụng**: `Spring Cloud Config` hỗ trợ cấu hình cho nhiều loại ứng dụng khác nhau như Spring Boot, Node.js, Python, v.v.
- **Tích hợp với nhiều môi trường**: `Spring Cloud Config` hỗ trợ cấu hình cho nhiều môi trường khác nhau như Development, Staging, Production, v.v.

## 2. Xây dựng Spring Cloud Config Server
### 2.1. Tạo Spring Boot Project
Chúng ta tạo một Spring Boot Project với dependencies sau:
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```
Sau khi mở `project` ta thêm annotation `@EnableConfigServer` vào class `Application` để khai báo `Spring Cloud Config Server`:
```java
@SpringBootApplication
@EnableConfigServer
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## 3. Đọc thông tin cấu hình từ Spring Cloud Config Server
`Spring Cloud Config` cung cấp một REST API để lấy thông tin cấu hình từ `Spring Cloud Config Server`. 

Để đọc thông tin cấu hình, `Spring Cloud Config Server` có 3 cách sau:

### 3.1. Đọc thông tin cấu hình từ `classpath` của `Config Server`
`Classpath` chính là thư mục `resources` của `Spring Project`

Để đọc thông tin cấu hình từ `classpath`, trong `application.properties` của `Config Server` phải thêm thuộc tính:
    - `spring.profiles.active=native`: để Config Server biết cấu hình sẽ được lấy từ `classpath`.
    - `spring.cloud.config.server.native.searchLocations=classpath:/config`: để Config Server biết nơi lưu trữ cấu hình. Chính là thư mục `config` trong thư mục `resources`.

> Lưu ý rằng, các file cấu hình phải được đặt trong thư mục `config` của thư mục `resources`. Và tên file cấu hình phải theo định dạng: `{application}-{profile}.properties`. Trong đó:
- `{application}`: là tên ứng dụng, ví dụ: `accounts-service`.
- `{profile}`: là profile của ứng dụng, ví dụ: `dev`, `prod`, `test`.

Sau đó chúng ta có thể truy cập vào web browser với đường dẫn sau để lấy thông tin cấu hình:
```http
http://localhost:8888/{application}/{profile}
<!-- Nếu là profile mặc định thì /profile= /default -->
```