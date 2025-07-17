package com.ravensoft.backend.beans;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;

@Getter
@Setter
public class PlaceStats {
  private long totalPlaces;
  private long totalCategories;
  private String averageRating;
  private long totalRatings;

  public PlaceStats() {}

  public PlaceStats(long totalPlaces, long totalCategories, String averageRating, long totalRatings) {
    this.totalPlaces = totalPlaces;
    this.totalCategories = totalCategories;
    this.averageRating = averageRating;
    this.totalRatings = totalRatings;
  }
}
