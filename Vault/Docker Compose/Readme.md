# Tạo Vault cho Server

Sau khi chạy xong docker-compose.yml, tiến hành chạy lệnh sau để tạo config vault, cũng như set key
```sh
docker-compose -it exec vault sh
/bin/sh -c /vault-init.sh
```

Sau đó copy các thông tin in ra để sử dụng (LOẠI AUTH (token, approle), ROLE ID, SECRET ID)

# Sử dụng Vault trong Spring Boot

```yml bootstrap.yml
spring:
  cloud:
    vault:
      enabled: true # Enable Vault
      uri: http://localhost:8200 # URI của Vault
      authentication: approle # or token
      app-role: # or token
        role-id: 1e4b1b3b-1b1b-1b1b-1b1b-1b1b1b1b1b1b # ROLE ID
        secret-id: 1b1b1b1b-1b1b-1b1b-1b1b-1b1b1b1b1b1b # SECRET ID
      fail-fast: true # Fail fast if Vault is not available
      config: 
        lifecycle:
          enable: true # Dùng để refresh config khi config thay đổi
      generic:
        enabled: false # Tắt chức năng generic
```
      