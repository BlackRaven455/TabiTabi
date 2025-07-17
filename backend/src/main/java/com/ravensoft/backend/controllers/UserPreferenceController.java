package com.ravensoft.backend.controllers;

import com.ravensoft.backend.dto.CreateUserPreferenceRequest;
import com.ravensoft.backend.dto.UpdateUserPreferenceRequest;
import com.ravensoft.backend.dto.UserPreferenceDTO;
import com.ravensoft.backend.services.UserPreferenceService;
import com.ravensoft.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
@RestController
@RequestMapping("/api/users/me/preferences")
@RequiredArgsConstructor
public class UserPreferenceController {

  private final UserPreferenceService userPreferenceService;
  private final UserService userService;

  @GetMapping
  public ResponseEntity<List<UserPreferenceDTO>> getUserPreferences(Authentication authentication) {
    Integer userId = getCurrentUserId(authentication);
    List<UserPreferenceDTO> preferences = userPreferenceService.findUserPreferences(userId);
    return ResponseEntity.ok(preferences);
  }

  @GetMapping("/liked")
  public ResponseEntity<List<UserPreferenceDTO>> getLikedPlaces(
    @AuthenticationPrincipal UserDetails userDetails) {

    Integer userId = getUserIdFromUserDetails(userDetails);
    List<UserPreferenceDTO> preferences = userPreferenceService.findLikedPlaces(userId);
    return ResponseEntity.ok(preferences);
  }

  @GetMapping("/count")
  public ResponseEntity<Long> countLikedPlaces(Authentication authentication) {
    Integer userId = getCurrentUserId(authentication);
    Long count = userPreferenceService.countLikedPlaces(userId);
    return ResponseEntity.ok(count);
  }

  @PostMapping
  public ResponseEntity<UserPreferenceDTO> addUserPreference(
    Authentication authentication,
    @RequestBody @Valid CreateUserPreferenceRequest request) {
    Integer userId = getCurrentUserId(authentication);
    UserPreferenceDTO created = userPreferenceService.addUserPreference(userId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PutMapping("/{placeId}")
  public ResponseEntity<UserPreferenceDTO> updateUserPreference(
    Authentication authentication,
    @PathVariable Integer placeId,
    @RequestBody @Valid UpdateUserPreferenceRequest request) {

    Integer userId = getCurrentUserId(authentication);
    UserPreferenceDTO updated = userPreferenceService.updateUserPreference(userId, placeId, request);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{placeId}")
  public ResponseEntity<Void> deleteUserPreference(
    Authentication authentication,
    @PathVariable Integer placeId) {

    Integer userId = getCurrentUserId(authentication);
    userPreferenceService.deleteUserPreference(userId, placeId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{placeId}/exists")
  public ResponseEntity<Boolean> hasUserPreference(
    Authentication authentication,
    @PathVariable Integer placeId) {

    Integer userId = getCurrentUserId(authentication);
    boolean exists = userPreferenceService.hasUserPreference(userId, placeId);
    return ResponseEntity.ok(exists);
  }

  private Integer getCurrentUserId(Authentication authentication) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    return getUserIdFromUserDetails(userDetails);
  }

  private Integer getUserIdFromUserDetails(UserDetails userDetails) {
    String email = userDetails.getUsername();
    return userService.findUserIdByEmail(email);
  }
}
