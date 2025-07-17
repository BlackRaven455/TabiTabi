package com.ravensoft.backend.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

@Getter
@Setter
public class PlacesDTO {

  private Integer placeId;

  @NotBlank(message = "Name is required")
  private String name;

  private String description;

  @NotBlank(message = "Location is required")
  private String location;

  private String imageUrl;

  @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
  @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
  private Double latitude;

  @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
  @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
  private Double longitude;

  private String googleId;
  private String rating;
  private String userTotal;

  @NotNull(message = "Category ID is required")
  private Integer categoryId;

  private String categoryName;
  private Double distanceKm;

  public PlacesDTO() {}

  public PlacesDTO(String name, String description, String location, Integer categoryId) {
    this.name = name;
    this.description = description;
    this.location = location;
    this.categoryId = categoryId;
  }

  @Override
  public String toString() {
    return "PlacesDTO{" +
      "placeId=" + placeId +
      ", name='" + name + '\'' +
      ", location='" + location + '\'' +
      ", categoryId=" + categoryId +
      ", rating='" + rating + '\'' +
      '}';
  }
}


