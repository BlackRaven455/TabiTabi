package com.ravensoft.backend.services;

import com.ravensoft.backend.beans.PlaceStats;
import com.ravensoft.backend.dao.CategoryRepository;
import com.ravensoft.backend.dao.PlacesRepository;
import com.ravensoft.backend.dto.PlacesDTO;
import com.ravensoft.backend.entity.Category;
import com.ravensoft.backend.entity.Places;
import com.ravensoft.backend.exceptions.CategoryNotFoundException;
import com.ravensoft.backend.exceptions.DuplicateGoogleIdException;
import com.ravensoft.backend.exceptions.PlaceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlacesService {

  private static final Logger log = LoggerFactory.getLogger(PlacesService.class);

  @Autowired
  private PlacesRepository placesRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  public List<PlacesDTO> getAllPlacesDTO() {
    log.info("Starting getAllPlacesDTO");

    try {
      List<Places> places = placesRepository.findAll();
      log.info("Found {} places in database", places.size());

      List<PlacesDTO> result = places.stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());

      log.info("Successfully converted {} places to DTO", result.size());
      return result;

    } catch (Exception e) {
      log.error("Error in getAllPlacesDTO", e);
      throw new RuntimeException("Failed to fetch places", e);
    }
  }

  public List<PlacesDTO> searchPlacesByNameDTO(String name) {
    return placesRepository.findByNameContainingIgnoreCase(name)
      .stream()
      .map(this::convertToDTO)
      .collect(Collectors.toList());
  }

  public List<PlacesDTO> getPlacesByCategoryDTO(int categoryId) {
    return placesRepository.findByCategoryId(categoryId)
      .stream()
      .map(this::convertToDTO)
      .collect(Collectors.toList());
  }

  public List<PlacesDTO> searchPlacesByLocationDTO(String location) {
    return placesRepository.findByLocationContainingIgnoreCase(location)
      .stream()
      .map(this::convertToDTO)
      .collect(Collectors.toList());
  }

  public List<PlacesDTO> getNearbyPlacesDTO(double latitude, double longitude, double radiusKm) {
    List<Places> places = placesRepository.findNearbyPlaces(latitude, longitude, radiusKm);

    return places.stream()
      .map(place -> {
        PlacesDTO dto = convertToDTO(place);
        double distance = calculateDistance(latitude, longitude,
          place.getLatitude(), place.getLongitude());
        dto.setDistanceKm(Math.round(distance * 100.0) / 100.0);
        return dto;
      })
      .collect(Collectors.toList());
  }

  public List<PlacesDTO> getTopRatedPlacesDTO(int limit) {
    return placesRepository.findTopRatedPlaces(limit)
      .stream()
      .map(this::convertToDTO)
      .collect(Collectors.toList());
  }

  public List<PlacesDTO> getRandomPlacesDTO(int limit) {
    return placesRepository.findRandomPlaces(limit)
      .stream()
      .map(this::convertToDTO)
      .collect(Collectors.toList());
  }

  public List<PlacesDTO> searchPlacesDTO(String searchTerm) {
    return placesRepository.findByNameOrDescriptionContaining(searchTerm)
      .stream()
      .map(this::convertToDTO)
      .collect(Collectors.toList());
  }

  public Optional<PlacesDTO> findByIdDTO(int id) {
    return placesRepository.findById(id)
      .map(this::convertToDTO);
  }

  public Page<Places> findAll(Pageable pageable) {
    return placesRepository.findAll(pageable);
  }

  public List<Places> findAll() {
    return placesRepository.findAll();
  }

  public Optional<Places> findById(int id) {
    return placesRepository.findById(id);
  }

  public Places save(Places place) {
    if (place.getCategory() != null && place.getCategory().getCategoryId() != 0) {
      Category category = categoryRepository.findById(place.getCategory().getCategoryId())
        .orElseThrow(() -> new CategoryNotFoundException(place.getCategory().getCategoryId()));
      place.setCategory(category);
    }

    if (place.getGoogleId() != null && !place.getGoogleId().isEmpty()) {
      Optional<Places> existingPlace = placesRepository.findByGoogleId(place.getGoogleId());
      if (existingPlace.isPresent() && existingPlace.get().getPlaceId() != place.getPlaceId()) {
        throw new DuplicateGoogleIdException(place.getGoogleId());
      }
    }

    return placesRepository.save(place);
  }

  public void deleteById(int id) {
    if (!placesRepository.existsById(id)) {
      throw new PlaceNotFoundException(id);
    }
    placesRepository.deleteById(id);
  }

  public boolean existsById(int id) {
    return placesRepository.existsById(id);
  }

  public Places partialUpdate(int id, Places updates) {
    Places existingPlace = placesRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Place not found"));

    if (updates.getName() != null) {
      existingPlace.setName(updates.getName());
    }
    if (updates.getDescription() != null) {
      existingPlace.setDescription(updates.getDescription());
    }
    if (updates.getLocation() != null) {
      existingPlace.setLocation(updates.getLocation());
    }
    if (updates.getImageUrl() != null) {
      existingPlace.setImageUrl(updates.getImageUrl());
    }
    if (updates.getLatitude() != null) {
      existingPlace.setLatitude(updates.getLatitude());
    }
    if (updates.getLongitude() != null) {
      existingPlace.setLongitude(updates.getLongitude());
    }
    if (updates.getGoogleId() != null) {
      existingPlace.setGoogleId(updates.getGoogleId());
    }
    if (updates.getRating() != null) {
      existingPlace.setRating(updates.getRating());
    }
    if (updates.getUserTotal() != null) {
      existingPlace.setUserTotal(updates.getUserTotal());
    }
    if (updates.getCategory() != null) {
      existingPlace.setCategory(updates.getCategory());
    }

    return placesRepository.save(existingPlace);
  }

  public List<Places> findByNameContainingIgnoreCase(String name) {
    return placesRepository.findByNameContainingIgnoreCase(name);
  }

  public List<Places> findByCategoryId(int categoryId) {
    return placesRepository.findByCategoryId(categoryId);
  }

  public List<Places> findByLocationContainingIgnoreCase(String location) {
    return placesRepository.findByLocationContainingIgnoreCase(location);
  }

  public List<Places> findByRatingGreaterThan(String rating) {
    return placesRepository.findByRatingGreaterThan(rating);
  }

  public Optional<Places> findByGoogleId(String googleId) {
    return placesRepository.findByGoogleId(googleId);
  }

  public List<Places> findNearbyPlaces(double latitude, double longitude, double radiusKm) {
    return placesRepository.findNearbyPlaces(latitude, longitude, radiusKm);
  }

  public List<Places> findTopRatedPlaces(int limit) {
    return placesRepository.findTopRatedPlaces(limit);
  }

  public PlaceStats getPlaceStats() {
    long totalPlaces = placesRepository.count();
    long totalCategories = placesRepository.countDistinctCategories();
    String averageRating = placesRepository.getAverageRating();
    long totalRatings = placesRepository.getTotalRatings();

    return new PlaceStats(totalPlaces, totalCategories, averageRating, totalRatings);
  }


  private PlacesDTO convertToDTO(Places place) {
    try {
      PlacesDTO dto = new PlacesDTO();
      dto.setPlaceId(place.getPlaceId());
      dto.setName(place.getName());
      dto.setDescription(place.getDescription());
      dto.setLocation(place.getLocation());
      dto.setImageUrl(place.getImageUrl());
      dto.setLatitude(place.getLatitude());
      dto.setLongitude(place.getLongitude());
      dto.setGoogleId(place.getGoogleId());

      dto.setRating(place.getRating() != null ? place.getRating().toString() : null);
      dto.setUserTotal(place.getUserTotal() != null ? place.getUserTotal().toString() : null);

      dto.setCategoryId(place.getCategoryId());

      if (place.getCategory() != null) {
        dto.setCategoryName(place.getCategory().getName());
      }

      return dto;

    } catch (Exception e) {
      log.error("Error converting place to DTO: {}", place.getPlaceId(), e);
      throw new RuntimeException("Failed to convert place to DTO", e);
    }
  }

  public Places convertFromDTO(PlacesDTO dto) {
    Places place = new Places();

    if (dto.getPlaceId() != null) {
      place.setPlaceId(dto.getPlaceId());
    }

    place.setName(dto.getName());
    place.setDescription(dto.getDescription());
    place.setLocation(dto.getLocation());
    place.setImageUrl(dto.getImageUrl());
    place.setLatitude(dto.getLatitude());
    place.setLongitude(dto.getLongitude());
    place.setGoogleId(dto.getGoogleId());

    if (dto.getRating() != null && !dto.getRating().isEmpty()) {
      try {
        place.setRating(String.valueOf(Double.parseDouble(dto.getRating())));
      } catch (NumberFormatException e) {
        log.warn("Invalid rating format: {}", dto.getRating());
      }
    }

    if (dto.getUserTotal() != null && !dto.getUserTotal().isEmpty()) {
      try {
        place.setUserTotal(String.valueOf(Integer.parseInt(dto.getUserTotal())));
      } catch (NumberFormatException e) {
        log.warn("Invalid userTotal format: {}", dto.getUserTotal());
      }
    }

    place.setCategoryId(dto.getCategoryId());

    if (dto.getCategoryId() != null) {
      Category category = categoryRepository.findById(dto.getCategoryId())
        .orElseThrow(() -> new CategoryNotFoundException(dto.getCategoryId()));
      place.setCategory(category);
    }

    return place;
  }

  public PlacesDTO saveFromDTO(PlacesDTO dto) {
    Places place = convertFromDTO(dto);
    Places savedPlace = save(place);
    return convertToDTO(savedPlace);
  }

  public PlacesDTO updateFromDTO(int id, PlacesDTO dto) {
    Places existingPlace = placesRepository.findById(id)
      .orElseThrow(() -> new PlaceNotFoundException(id));

    Places updatedPlace = convertFromDTO(dto);
    updatedPlace.setPlaceId(id);

    Places savedPlace = save(updatedPlace);
    return convertToDTO(savedPlace);
  }


  private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    final int R = 6371;

    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);

    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
      + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
      * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return R * c;
  }
}
