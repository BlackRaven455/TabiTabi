package com.ravensoft.backend.entity;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "places")
@Data
public class Places {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "place_id")
  private int placeId;

  @Column(name = "name")
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "location")
  private String location;

  @Column(name = "image_url")
  private String imageUrl;

  @Column(name = "latitude")
  private Double latitude;

  @Column(name = "longitude")
  private Double longitude;

  @Column(name = "google_place_id")
  private String googleId;

  @Column(name="rating")
  private String rating;

  @Column(name = "user_ratings_total")
  private String userTotal;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @Column(name = "category_id", insertable = false, updatable = false)
  private Integer categoryId;


}
