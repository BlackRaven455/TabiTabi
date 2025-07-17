package com.ravensoft.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserPreferenceRequest {
  @NotNull
  private Integer placeId;

  @NotNull
  private Boolean isLiked;
}
