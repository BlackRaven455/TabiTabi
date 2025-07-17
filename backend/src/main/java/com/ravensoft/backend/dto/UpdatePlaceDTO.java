package com.ravensoft.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePlaceDTO {
  private String name;
  private String description;
  private String location;
  private String imageUrl;
  private Double latitude;
  private Double longitude;
  private String googleId;
  private String rating;
  private String userTotal;
  private Integer categoryId;
}
