package com.ravensoft.backend.services;

import com.ravensoft.backend.dao.PlacesRepository;
import com.ravensoft.backend.dao.UserPreferenceRepository;
import com.ravensoft.backend.dao.UserRepository;
import com.ravensoft.backend.dto.CreateUserPreferenceRequest;
import com.ravensoft.backend.dto.UpdateUserPreferenceRequest;
import com.ravensoft.backend.dto.UserPreferenceDTO;

import com.ravensoft.backend.entity.Userpreference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserPreferenceService {

  private final UserPreferenceRepository userPreferenceRepository;
  private final UserRepository userRepository;
  private final PlacesRepository placesRepository;

  @Transactional(readOnly = true)
  public List<UserPreferenceDTO> findUserPreferences(Integer userId) {
    if (!userRepository.existsById(userId)) {
      throw new EntityNotFoundException("User not found with id: " + userId);
    }

    List<Userpreference> preferences = userPreferenceRepository.findByUserIdWithPlace(userId);
    return preferences.stream()
      .map(this::convertToDto)
      .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<UserPreferenceDTO> findLikedPlaces(Integer userId) {
    if (!userRepository.existsById(userId)) {
      throw new EntityNotFoundException("User not found with id: " + userId);
    }

    List<Userpreference> preferences = userPreferenceRepository.findByUserIdAndIsLiked(userId, true);
    return preferences.stream()
      .map(this::convertToDto)
      .collect(Collectors.toList());
  }

  public UserPreferenceDTO addUserPreference(Integer userId, CreateUserPreferenceRequest request) {
    if (!userRepository.existsById(userId)) {
      throw new EntityNotFoundException("User not found with id: " + userId);
    }

    if (!placesRepository.existsById(request.getPlaceId())) {
      throw new EntityNotFoundException("Place not found with id: " + request.getPlaceId());
    }

    Optional<Userpreference> existing = userPreferenceRepository
      .findByUserIdAndPlaceId(userId, request.getPlaceId());

    if (existing.isPresent()) {
      throw new IllegalArgumentException("User preference already exists for this place");
    }

    Userpreference preference = new Userpreference();
    preference.setUser(userRepository.getReferenceById(userId));
    preference.setPlace(placesRepository.getReferenceById(request.getPlaceId()));
    preference.setIsLiked(request.getIsLiked());
    preference.setCreatedAt(Instant.now());

    Userpreference saved = userPreferenceRepository.save(preference);
    return convertToDto(saved);
  }

  public UserPreferenceDTO updateUserPreference(Integer userId, Integer placeId, UpdateUserPreferenceRequest request) {
    Userpreference preference = userPreferenceRepository
      .findByUserIdAndPlaceId(userId, placeId)
      .orElseThrow(() -> new EntityNotFoundException("User preference not found"));

    preference.setIsLiked(request.getIsLiked());

    Userpreference updated = userPreferenceRepository.save(preference);
    return convertToDto(updated);
  }

  public void deleteUserPreference(Integer userId, Integer placeId) {
    Userpreference preference = userPreferenceRepository
      .findByUserIdAndPlaceId(userId, placeId)
      .orElseThrow(() -> new EntityNotFoundException("User preference not found"));

    userPreferenceRepository.delete(preference);
  }

  @Transactional(readOnly = true)
  public Long countLikedPlaces(Integer userId) {
    if (!userRepository.existsById(userId)) {
      throw new EntityNotFoundException("User not found with id: " + userId);
    }

    return userPreferenceRepository.countLikedByUserId(userId);
  }

  @Transactional(readOnly = true)
  public boolean hasUserPreference(Integer userId, Integer placeId) {
    return userPreferenceRepository.existsByUserIdAndPlacePlaceId(userId, placeId);
  }

  private UserPreferenceDTO convertToDto(Userpreference preference) {
    return UserPreferenceDTO.builder()
      .id(preference.getId())
      .userId(preference.getUser().getId())
      .placeId(preference.getPlace().getPlaceId())
      .placeName(preference.getPlace().getName())
      .isLiked(preference.getIsLiked())
      .createdAt(preference.getCreatedAt())
      .build();
  }
}
