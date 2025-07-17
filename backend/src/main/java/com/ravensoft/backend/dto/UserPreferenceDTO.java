package com.ravensoft.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UserPreferenceDTO {

  private Integer id;
  private Integer userId;
  private Integer placeId;
  private String placeName;
  private Boolean isLiked;
  private Instant createdAt;
}

