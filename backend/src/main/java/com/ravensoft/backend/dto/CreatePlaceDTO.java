package com.ravensoft.backend.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreatePlaceDTO {
  @NotBlank(message = "Name is required")
  private String name;

  private String description;

  @NotBlank(message = "Location is required")
  private String location;

  private String imageUrl;
  private Double latitude;
  private Double longitude;
  private String googleId;
  private String rating;
  private String userTotal;

  @NotNull(message = "Category ID is required")
  private Integer categoryId;
}
