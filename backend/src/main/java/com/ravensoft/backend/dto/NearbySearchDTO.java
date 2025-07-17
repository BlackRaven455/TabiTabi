package com.ravensoft.backend.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class NearbySearchDTO {
  @NotNull(message = "Latitude is required")
  @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
  @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
  private Double latitude;

  @NotNull(message = "Longitude is required")
  @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
  @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
  private Double longitude;

  @NotNull(message = "Radius is required")
  @DecimalMin(value = "0.1", message = "Radius must be at least 0.1 km")
  @DecimalMax(value = "100.0", message = "Radius cannot exceed 100 km")
  private Double radiusKm;

  private Integer categoryId;
}
