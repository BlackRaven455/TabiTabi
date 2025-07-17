package com.ravensoft.backend.dao;


import com.ravensoft.backend.entity.Places;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlacesRepository extends JpaRepository<Places, Integer> {

  List<Places> findByNameContainingIgnoreCase(String name);

  @Query("SELECT p FROM Places p WHERE p.category.categoryId = :categoryId")
  List<Places> findByCategoryId(@Param("categoryId") int categoryId);

  List<Places> findByLocationContainingIgnoreCase(String location);

  @Query("SELECT p FROM Places p WHERE CAST(p.rating AS double) > CAST(:rating AS double)")
  List<Places> findByRatingGreaterThan(@Param("rating") String rating);

  Optional<Places> findByGoogleId(String googleId);

  @Query(value = "SELECT * FROM places p WHERE " +
    "(6371 * acos(cos(radians(:latitude)) * cos(radians(p.latitude)) * " +
    "cos(radians(p.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
    "sin(radians(p.latitude)))) <= :radiusKm " +
    "AND p.latitude IS NOT NULL AND p.longitude IS NOT NULL",
    nativeQuery = true)
  List<Places> findNearbyPlaces(@Param("latitude") double latitude,
                                @Param("longitude") double longitude,
                                @Param("radiusKm") double radiusKm);

  @Query("SELECT p FROM Places p WHERE p.rating IS NOT NULL " +
    "ORDER BY CAST(p.rating AS double) DESC")
  List<Places> findTopRatedPlaces(Pageable pageable);

  default List<Places> findTopRatedPlaces(int limit) {
    return findTopRatedPlaces(org.springframework.data.domain.PageRequest.of(0, limit));
  }

  @Query("SELECT COUNT(DISTINCT p.category.categoryId) FROM Places p")
  long countDistinctCategories();

  @Query("SELECT AVG(CAST(p.rating AS double)) FROM Places p WHERE p.rating IS NOT NULL")
  String getAverageRating();

  @Query("SELECT SUM(CAST(p.userTotal AS long)) FROM Places p WHERE p.userTotal IS NOT NULL")
  long getTotalRatings();

  @Query("SELECT p FROM Places p WHERE p.latitude BETWEEN :minLat AND :maxLat " +
    "AND p.longitude BETWEEN :minLng AND :maxLng")
  List<Places> findByCoordinatesRange(@Param("minLat") double minLat,
                                      @Param("maxLat") double maxLat,
                                      @Param("minLng") double minLng,
                                      @Param("maxLng") double maxLng);

  @Query("SELECT p FROM Places p WHERE p.imageUrl IS NULL OR p.imageUrl = ''")
  List<Places> findPlacesWithoutImages();

  @Query("SELECT p FROM Places p WHERE p.latitude IS NULL OR p.longitude IS NULL")
  List<Places> findPlacesWithoutCoordinates();

  @Query("SELECT p FROM Places p WHERE p.category.categoryId IN :categoryIds")
  List<Places> findByMultipleCategories(@Param("categoryIds") List<Integer> categoryIds);

  @Query("SELECT p FROM Places p WHERE " +
    "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
    "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
  List<Places> findByNameOrDescriptionContaining(@Param("searchTerm") String searchTerm);

  @Query("SELECT p FROM Places p WHERE " +
    "CAST(p.rating AS double) >= :minRating AND " +
    "CAST(p.userTotal AS long) >= :minReviews")
  List<Places> findHighRatedPlacesWithManyReviews(@Param("minRating") double minRating,
                                                  @Param("minReviews") long minReviews);

  @Query(value = "SELECT * FROM places ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
  List<Places> findRandomPlaces(@Param("limit") int limit);


}
