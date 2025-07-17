package com.ravensoft.backend.controllers;

import com.ravensoft.backend.beans.PlaceStats;
import com.ravensoft.backend.dto.CreatePlaceDTO;
import com.ravensoft.backend.dto.NearbySearchDTO;
import com.ravensoft.backend.dto.PlacesDTO;
import com.ravensoft.backend.dto.UpdatePlaceDTO;
import com.ravensoft.backend.services.PlacesService;
import com.ravensoft.backend.exceptions.PlaceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/api/places")
@CrossOrigin(origins = "http://localhost:4200")
public class PlacesController {

  private static final Logger log = LoggerFactory.getLogger(PlacesController.class);

  @Autowired
  private PlacesService placesService;

  @GetMapping("/all")
  public ResponseEntity<List<PlacesDTO>> getAllPlaces() {
    try {
      log.info("GET /api/places/all requested");
      List<PlacesDTO> places = placesService.getAllPlacesDTO();
      log.info("Returning {} places", places.size());
      return ResponseEntity.ok(places);
    } catch (Exception e) {
      log.error("Error in getAllPlaces endpoint", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<PlacesDTO> getPlaceById(@PathVariable int id) {
    try {
      Optional<PlacesDTO> place = placesService.findByIdDTO(id);
      return place.map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
    } catch (Exception e) {
      log.error("Error getting place by id: {}", id, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/search")
  public ResponseEntity<List<PlacesDTO>> searchPlaces(@RequestParam String q) {
    try {
      List<PlacesDTO> places = placesService.searchPlacesDTO(q);
      return ResponseEntity.ok(places);
    } catch (Exception e) {
      log.error("Error searching places with query: {}", q, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/search/name")
  public ResponseEntity<List<PlacesDTO>> searchPlacesByName(@RequestParam String name) {
    try {
      List<PlacesDTO> places = placesService.searchPlacesByNameDTO(name);
      return ResponseEntity.ok(places);
    } catch (Exception e) {
      log.error("Error searching places by name: {}", name, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/search/location")
  public ResponseEntity<List<PlacesDTO>> searchPlacesByLocation(@RequestParam String location) {
    try {
      List<PlacesDTO> places = placesService.searchPlacesByLocationDTO(location);
      return ResponseEntity.ok(places);
    } catch (Exception e) {
      log.error("Error searching places by location: {}", location, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/category/{categoryId}")
  public ResponseEntity<List<PlacesDTO>> getPlacesByCategory(@PathVariable int categoryId) {
    try {
      List<PlacesDTO> places = placesService.getPlacesByCategoryDTO(categoryId);
      return ResponseEntity.ok(places);
    } catch (Exception e) {
      log.error("Error getting places by category: {}", categoryId, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PostMapping("/nearby")
  public ResponseEntity<List<PlacesDTO>> getNearbyPlaces(@RequestBody @Valid NearbySearchDTO request) {
    try {
      List<PlacesDTO> places = placesService.getNearbyPlacesDTO(
        request.getLatitude(),
        request.getLongitude(),
        request.getRadiusKm()
      );
      return ResponseEntity.ok(places);
    } catch (Exception e) {
      log.error("Error getting nearby places", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/top-rated")
  public ResponseEntity<List<PlacesDTO>> getTopRatedPlaces(@RequestParam(defaultValue = "10") int limit) {
    try {
      List<PlacesDTO> places = placesService.getTopRatedPlacesDTO(limit);
      return ResponseEntity.ok(places);
    } catch (Exception e) {
      log.error("Error getting top rated places", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/random")
  public ResponseEntity<List<PlacesDTO>> getRandomPlaces(@RequestParam(defaultValue = "5") int limit) {
    try {
      List<PlacesDTO> places = placesService.getRandomPlacesDTO(limit);
      return ResponseEntity.ok(places);
    } catch (Exception e) {
      log.error("Error getting random places", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PostMapping
  public ResponseEntity<PlacesDTO> createPlace(@RequestBody @Valid CreatePlaceDTO createPlaceDTO) {
    try {
      PlacesDTO placesDTO = new PlacesDTO();
      placesDTO.setName(createPlaceDTO.getName());
      placesDTO.setDescription(createPlaceDTO.getDescription());
      placesDTO.setLocation(createPlaceDTO.getLocation());
      placesDTO.setImageUrl(createPlaceDTO.getImageUrl());
      placesDTO.setLatitude(createPlaceDTO.getLatitude());
      placesDTO.setLongitude(createPlaceDTO.getLongitude());
      placesDTO.setGoogleId(createPlaceDTO.getGoogleId());
      placesDTO.setRating(createPlaceDTO.getRating());
      placesDTO.setUserTotal(createPlaceDTO.getUserTotal());
      placesDTO.setCategoryId(createPlaceDTO.getCategoryId());

      PlacesDTO savedPlace = placesService.saveFromDTO(placesDTO);
      return ResponseEntity.status(HttpStatus.CREATED).body(savedPlace);
    } catch (Exception e) {
      log.error("Error creating place", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<PlacesDTO> updatePlace(@PathVariable int id, @RequestBody @Valid UpdatePlaceDTO updatePlaceDTO) {
    try {
      PlacesDTO placesDTO = new PlacesDTO();
      placesDTO.setName(updatePlaceDTO.getName());
      placesDTO.setDescription(updatePlaceDTO.getDescription());
      placesDTO.setLocation(updatePlaceDTO.getLocation());
      placesDTO.setImageUrl(updatePlaceDTO.getImageUrl());
      placesDTO.setLatitude(updatePlaceDTO.getLatitude());
      placesDTO.setLongitude(updatePlaceDTO.getLongitude());
      placesDTO.setGoogleId(updatePlaceDTO.getGoogleId());
      placesDTO.setRating(updatePlaceDTO.getRating());
      placesDTO.setUserTotal(updatePlaceDTO.getUserTotal());
      placesDTO.setCategoryId(updatePlaceDTO.getCategoryId());

      PlacesDTO updatedPlace = placesService.updateFromDTO(id, placesDTO);
      return ResponseEntity.ok(updatedPlace);
    } catch (PlaceNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      log.error("Error updating place: {}", id, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePlace(@PathVariable int id) {
    try {
      placesService.deleteById(id);
      return ResponseEntity.noContent().build();
    } catch (PlaceNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      log.error("Error deleting place: {}", id, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/stats")
  public ResponseEntity<PlaceStats> getPlaceStats() {
    try {
      PlaceStats stats = placesService.getPlaceStats();
      return ResponseEntity.ok(stats);
    } catch (Exception e) {
      log.error("Error getting place stats", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
