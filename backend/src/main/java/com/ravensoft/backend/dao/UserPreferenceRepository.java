package com.ravensoft.backend.dao;

import com.ravensoft.backend.entity.Userpreference;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPreferenceRepository extends JpaRepository<Userpreference, Integer> {

  List<Userpreference> findByUserId(Integer userId);
  List<Userpreference> findByUserIdAndIsLiked(Integer userId, Boolean isLiked);
  boolean existsByUserIdAndPlacePlaceId(Integer userId, Integer placeId);
  long countByUserIdAndIsLiked(Integer userId, Boolean isLiked);

  @Query("SELECT up FROM Userpreference up WHERE up.user.id = :userId AND up.place.placeId = :placeId")
  Optional<Userpreference> findByUserIdAndPlaceId(@Param("userId") Integer userId, @Param("placeId") Integer placeId);
  @Query("SELECT up FROM Userpreference up JOIN FETCH up.place WHERE up.user.id = :userId")
  List<Userpreference> findByUserIdWithPlace(@Param("userId") Integer userId);

  @Query("SELECT COUNT(up) FROM Userpreference up WHERE up.user.id = :userId AND up.isLiked = true")
  Long countLikedByUserId(@Param("userId") Integer userId);

  @Query("SELECT up FROM Userpreference up JOIN FETCH up.place JOIN FETCH up.user " +
    "WHERE up.user.id = :userId AND up.isLiked = :isLiked")
  List<Userpreference> findByUserIdAndIsLikedWithPlace(@Param("userId") Integer userId,
                                                       @Param("isLiked") Boolean isLiked);
}
