package com.didan.schedule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponseDTO {

  @JsonProperty("id")
  private String userId;
  private String name;
  private String avatar;
  private String createdAt;
}
