package com.didan.microservices.gatewaysever.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

  @Override
  public Collection<GrantedAuthority> convert(Jwt source) {
    Map<String, Object> realmAccess = (Map<String, Object>) source.getClaims().get("realm_access"); // Lấy thông tin về các role từ claim realm_access trong body của JWT
    if (realmAccess == null || realmAccess.isEmpty()) { // Nếu không có role nào được trả về thì trả về một danh sách rỗng
      return new ArrayList<>();
    }
    Collection<GrantedAuthority> returnValue = ((List<String>) realmAccess.get("roles")) // Lấy danh sách các role từ realm_access.roles
        .stream() // Chuyển danh sách các role thành stream để thực hiện các thao tác tiếp theo
        .map(roleName -> "ROLE_" + roleName) // Thêm tiền tố ROLE_ vào tên của role
        .map(SimpleGrantedAuthority::new) // Chuyển tên của role thành một SimpleGrantedAuthority object
        .collect(Collectors.toList()); // Chuyển stream thành List
    return returnValue; // Trả về danh sách các role
  }
}
