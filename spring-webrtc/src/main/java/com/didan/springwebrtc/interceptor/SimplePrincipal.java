package com.didan.springwebrtc.interceptor;

import java.security.Principal;
import javax.security.auth.Subject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dannd1
 * @since 7/11/2025
 */
@AllArgsConstructor
@Data
public class SimplePrincipal implements Principal {

  private final String name;

  @Override
  public String getName() {
    return this.name;
  }
}
