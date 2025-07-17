package com.ravensoft.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ravensoft.backend.dao.PlacesRepository;
import com.ravensoft.backend.dao.CategoryRepository;
import com.ravensoft.backend.entity.Places;
import com.ravensoft.backend.entity.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripAdvisorService {

  private final PlacesRepository placesRepository;
  private final CategoryRepository categoryRepository;
  private final ObjectMapper objectMapper;
  private final WebClient tripAdvisorWebClient;

  @Value("${tripadvisor.api.key}")
  private String apiKey;

  @Value("${tripadvisor.api.host}")
  private String apiHost;

  /**
   * Reactive method to fetch and save attractions
   * Returns number of saved places
   */
  public Mono<Integer> fetchAndSaveAttractions(String tripAdvisorId, String startDate, String endDate) {
    log.info("Fetching attractions for geoId: {}, dates: {} to {}", tripAdvisorId, startDate, endDate);

    return tripAdvisorWebClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/attractions/search")
        .queryParam("geoId", tripAdvisorId)
        .queryParam("startDate", startDate)
        .queryParam("endDate", endDate)
        .build())
      .retrieve()
      .onStatus(
        status -> status.is4xxClientError() || status.is5xxServerError(),
        clientResponse -> {
          log.error("API error: {} - {}", clientResponse.statusCode(), clientResponse.statusCode());
          return clientResponse.bodyToMono(String.class)
            .flatMap(errorBody -> {
              log.error("Error response body: {}", errorBody);
              return Mono.error(new RuntimeException("API Error: " + clientResponse.statusCode()));
            });
        })
      .bodyToMono(String.class)
      .timeout(Duration.ofSeconds(30))
      .doOnSubscribe(subscription -> log.debug("Starting API call to TripAdvisor"))
      .doOnNext(response -> {
        log.debug("Received response from TripAdvisor API");
        log.debug("Response body: {}", response);
      })
      .flatMap(this::parseAndSave)
      .doOnSuccess(count -> log.info("Successfully saved {} attractions", count))
      .doOnError(error -> log.error("Failed to fetch and save attractions", error))
      .onErrorResume(this::handleApiError);
  }

  /**
   * Alternative method that returns Mono for reactive chains
   */
  public Mono<Integer> fetchAndSaveAttractionsReactive(String tripAdvisorId, String startDate, String endDate) {
    return fetchAndSaveAttractions(tripAdvisorId, startDate, endDate);
  }

  /**
   * Blocking method for traditional service calls
   */
  public int fetchAndSaveAttractionsBlocking(String tripAdvisorId, String startDate, String endDate) {
    try {
      return fetchAndSaveAttractions(tripAdvisorId, startDate, endDate)
        .block(Duration.ofMinutes(2));
    } catch (Exception e) {
      log.error("Error in blocking call", e);
      return 0;
    }
  }

  private Mono<Integer> parseAndSave(String json) {
    return Mono.fromCallable(() -> parseJsonToPlaces(json))
      .flatMap(this::savePlacesBatch)
      .subscribeOn(Schedulers.boundedElastic())
      .doOnSuccess(count -> log.debug("Parsed and saved {} places", count))
      .doOnError(error -> log.error("Failed to parse and save JSON", error));
  }

  private List<Places> parseJsonToPlaces(String json) {
    List<Places> places = new ArrayList<>();

    try {
      log.debug("Parsing JSON response: {}", json);
      JsonNode root = objectMapper.readTree(json);

      JsonNode attractions = null;

      if (root.has("data") && root.get("data").has("attractions")) {
        attractions = root.path("data").path("attractions");
      } else if (root.has("data") && root.get("data").isArray()) {
        attractions = root.path("data");
      } else if (root.has("attractions")) {
        attractions = root.path("attractions");
      } else if (root.isArray()) {
        attractions = root;
      }

      if (attractions == null || attractions.isMissingNode()) {
        log.warn("No attractions found in response structure");
        log.debug("Response structure: {}", root.toString());
        return places;
      }

      if (!attractions.isArray()) {
        log.warn("Attractions node is not an array");
        return places;
      }

      log.debug("Found {} attractions to process", attractions.size());

      for (JsonNode node : attractions) {
        try {
          Places place = createPlaceFromNode(node);
          if (place != null && isValidPlace(place)) {
            places.add(place);
            log.debug("Added place: {}", place.getName());
          } else {
            log.debug("Skipped invalid place: {}", node.toString());
          }
        } catch (Exception e) {
          log.warn("Failed to parse individual attraction: {}", e.getMessage());
          log.debug("Problematic node: {}", node.toString());
          // Continue processing other attractions
        }
      }

    } catch (Exception e) {
      log.error("Failed to parse JSON response", e);
      log.debug("JSON that failed to parse: {}", json);
      throw new RuntimeException("JSON parsing failed", e);
    }

    return places;
  }

  private Places createPlaceFromNode(JsonNode node) {
    log.debug("Processing node: {}", node.toString());
    String name = node.path("cardTitle").path("string").asText("");
    if (name.isEmpty()) {
      name = node.path("title").asText("");
      if (name.isEmpty()) {
        name = node.path("display_name").asText("");
      }
    }
    if (name.isEmpty()) {
      log.debug("Skipping place with empty name");
      return null;
    }
    Places place = new Places();
    place.setName(name);

    String description = node.path("primaryInfo").asText("");
    if (description.isEmpty()) {
      description = node.path("snippet").asText("");
    }
    if (description.isEmpty()) {
      description = node.path("web_url").asText("");
    }
    if (description.isEmpty()) {
      description = node.path("url").asText("");
    }
    if (description.isEmpty()) {
      description = "no description";
    }
    place.setDescription(description);

    String location = extractLocation(node);
    place.setLocation(location);

    // Handle image URL with multiple fallbacks
    String imageUrl = extractBestImageUrl(node);
    place.setImageUrl(imageUrl);

    // Handle coordinates with validation
    setValidatedCoordinates(place, node);

    // Handle rating and reviews
    place.setRating(extractRating(node));
    place.setUserTotal(extractReviewCount(node));

    // Handle category - получаем категорию и устанавливаем её ID
    Category category = getOrCreateCategory(extractCategoryName(node));
    place.setCategory(category);

    // Handle TripAdvisor location ID
    String locationId = extractLocationId(node);
    place.setGoogleId(locationId);

    // НЕ устанавливаем createdAt - его нет в сущности
    // place.setCreatedAt(LocalDateTime.now());

    return place;
  }

  private String extractLocation(JsonNode node) {
    String location = node.path("address").asText("");
    if (location.isEmpty()) {
      JsonNode addressObj = node.path("address_obj");
      if (!addressObj.isMissingNode()) {
        location = addressObj.path("address_string").asText("");
        if (location.isEmpty()) {
          location = addressObj.path("street1").asText("");
        }
      }
    }
    if (location.isEmpty()) {
      location = node.path("location_string").asText("");
    }
    return location;
  }

  private String extractLocationId(JsonNode node) {
    String locationId = node.path("location_id").asText("");
    if (locationId.isEmpty()) {
      locationId = node.path("id").asText("");
    }
    if (locationId.isEmpty()) {
      locationId = node.path("place_id").asText("");
    }
    return locationId;
  }

  private String extractRating(JsonNode node) {
    String rating = node.path("rating").asText("");
    if (rating.isEmpty()) {
      rating = node.path("overall_rating").asText("");
    }
    if (rating.isEmpty()) {
      rating = node.path("score").asText("");
    }
    return rating;
  }

  private String extractReviewCount(JsonNode node) {
    String reviewCount = node.path("num_reviews").asText("");
    if (reviewCount.isEmpty()) {
      reviewCount = node.path("review_count").asText("");
    }
    if (reviewCount.isEmpty()) {
      reviewCount = node.path("total_reviews").asText("");
    }
    return reviewCount;
  }

  private String extractBestImageUrl(JsonNode node) {

    JsonNode photoNode = node.path("photo");
    if (!photoNode.isMissingNode()) {
      JsonNode imagesNode = photoNode.path("images");
      if (!imagesNode.isMissingNode()) {
        String[] sizes = {"large", "medium", "small", "original"};
        for (String size : sizes) {
          String url = imagesNode.path(size).path("url").asText("");
          if (!url.isEmpty()) {
            return url;
          }
        }
      }

      String url = photoNode.path("url").asText("");
      if (!url.isEmpty()) {
        return url;
      }
    }

    String imageUrl = node.path("image_url").asText("");
    if (imageUrl.isEmpty()) {
      imageUrl = node.path("thumbnail").asText("");
    }
    if (imageUrl.isEmpty()) {
      imageUrl = node.path("picture").asText("");
    }

    return imageUrl;
  }

  private void setValidatedCoordinates(Places place, JsonNode node) {
    try {
      double latitude = 0.0;
      double longitude = 0.0;

      if (node.has("latitude") && node.has("longitude")) {
        latitude = node.path("latitude").asDouble(0.0);
        longitude = node.path("longitude").asDouble(0.0);
      } else if (node.has("lat") && node.has("lng")) {
        latitude = node.path("lat").asDouble(0.0);
        longitude = node.path("lng").asDouble(0.0);
      } else if (node.has("coordinates")) {
        JsonNode coords = node.path("coordinates");
        latitude = coords.path("latitude").asDouble(0.0);
        longitude = coords.path("longitude").asDouble(0.0);
      }

      if (isValidCoordinate(latitude, longitude)) {
        place.setLatitude(latitude);
        place.setLongitude(longitude);
      } else {
        log.debug("Invalid coordinates for place {}: lat={}, lng={}",
          place.getName(), latitude, longitude);
      }
    } catch (Exception e) {
      log.warn("Failed to set coordinates for place {}: {}",
        place.getName(), e.getMessage());
    }
  }

  private boolean isValidCoordinate(double lat, double lng) {
    return lat >= -90 && lat <= 90 &&
      lng >= -180 && lng <= 180 &&
      !(lat == 0.0 && lng == 0.0);
  }

  private void assignCategory(Places place, JsonNode node) {
    String categoryName = extractCategoryName(node);
    Category category = getOrCreateCategory(categoryName);
    place.setCategory(category);
  }

  private String extractCategoryName(JsonNode node) {
    String categoryName = node.path("category").path("name").asText("");

    if (categoryName.isEmpty()) {
      categoryName = node.path("subcategory").path("name").asText("");
    }

    if (categoryName.isEmpty()) {
      categoryName = node.path("category_name").asText("");
    }

    if (categoryName.isEmpty()) {
      categoryName = node.path("type").asText("");
    }

    if (categoryName.isEmpty()) {
      categoryName = inferCategoryFromName(node.path("name").asText(""));
    }

    return categoryName.isEmpty() ? "観光地" : categoryName;
  }

  private String inferCategoryFromName(String name) {
    String lowerName = name.toLowerCase();

    if (lowerName.contains("レストラン") || lowerName.contains("restaurant") ||
      lowerName.contains("カフェ") || lowerName.contains("cafe")) {
      return "レストラン";
    } else if (lowerName.contains("ホテル") || lowerName.contains("hotel")) {
      return "ホテル";
    } else if (lowerName.contains("博物館") || lowerName.contains("museum") ||
      lowerName.contains("美術館") || lowerName.contains("gallery")) {
      return "博物館";
    } else if (lowerName.contains("公園") || lowerName.contains("park") ||
      lowerName.contains("庭園") || lowerName.contains("garden")) {
      return "公園";
    } else if (lowerName.contains("神社") || lowerName.contains("shrine") ||
      lowerName.contains("寺") || lowerName.contains("temple")) {
      return "神社・寺院";
    } else {
      return "観光地";
    }
  }

  @Transactional
  protected Category getOrCreateCategory(String categoryName) {
    try {
      Optional<Category> existingCategory = categoryRepository.findByName(categoryName);

      if (existingCategory.isPresent()) {
        return existingCategory.get();
      }

      Category newCategory = new Category();
      newCategory.setName(categoryName);
      return categoryRepository.save(newCategory);
    } catch (Exception e) {
      log.error("Failed to get or create category: {}", categoryName, e);
      return getDefaultCategory();
    }
  }

  private Category getDefaultCategory() {
    try {
      Optional<Category> defaultCategory = categoryRepository.findByName("観光地");
      if (defaultCategory.isPresent()) {
        return defaultCategory.get();
      }

      Category newCategory = new Category();
      newCategory.setName("観光地");
      return categoryRepository.save(newCategory);
    } catch (Exception e) {
      log.error("Failed to create default category", e);
      throw new RuntimeException("Cannot create default category", e);
    }
  }

  private boolean isValidPlace(Places place) {
    boolean valid = place.getName() != null &&
      !place.getName().trim().isEmpty() &&
      place.getLatitude() != null &&
      place.getLongitude() != null;

    if (!valid) {
      log.debug("Place validation failed: name={}, lat={}, lng={}",
        place.getName(), place.getLatitude(), place.getLongitude());
    }

    return valid;
  }

  private Mono<Integer> savePlacesBatch(List<Places> places) {
    if (places.isEmpty()) {
      log.debug("No places to save");
      return Mono.just(0);
    }

    return Mono.fromCallable(() -> {
      try {
        List<Places> savedPlaces = placesRepository.saveAll(places);
        log.debug("Batch saved {} places to database", savedPlaces.size());
        return savedPlaces.size();
      } catch (Exception e) {
        log.error("Failed to save places batch", e);
        throw new RuntimeException("Database save failed", e);
      }
    }).subscribeOn(Schedulers.boundedElastic());
  }

  private Mono<Integer> handleApiError(Throwable error) {
    if (error instanceof WebClientResponseException webError) {
      log.error("TripAdvisor API error: {} - {}", webError.getStatusCode(), webError.getResponseBodyAsString());

      switch (webError.getStatusCode()) {
        case TOO_MANY_REQUESTS:
          log.warn("Rate limit exceeded, consider implementing backoff strategy");
          break;
        case UNAUTHORIZED:
          log.error("API key is invalid or expired");
          break;
        case FORBIDDEN:
          log.error("Access forbidden, check API permissions");
          break;
        default:
          log.error("HTTP error: {}", webError.getStatusCode());
      }
    } else {
      log.error("Network or processing error", error);
    }

    return Mono.just(0);
  }

  /**
   * Alternative method with retry logic
   */
  public Mono<Integer> fetchAndSaveAttractionsWithRetry(String tripAdvisorId, String startDate, String endDate) {
    return fetchAndSaveAttractions(tripAdvisorId, startDate, endDate)
      .retry(3) // Retry up to 3 times
      .doOnError(error -> log.error("Failed after 3 retries", error));
  }

  /**
   * Method to fetch specific location details
   */
  public Mono<String> fetchLocationDetails(String locationId) {
    return tripAdvisorWebClient.get()
      .uri("/location/{locationId}/details", locationId)
      .retrieve()
      .bodyToMono(String.class)
      .timeout(Duration.ofSeconds(30))
      .doOnSuccess(response -> log.debug("Fetched details for location: {}", locationId))
      .doOnError(error -> log.error("Failed to fetch location details for: {}", locationId, error));
  }

  public Mono<String> testApiConnection() {
    return tripAdvisorWebClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/attractions/search")
        .queryParam("geoId", "294232")
        .build())
      .retrieve()
      .bodyToMono(String.class)
      .timeout(Duration.ofSeconds(10))
      .doOnSuccess(response -> log.info("API connection test successful"))
      .doOnError(error -> log.error("API connection test failed", error));
  }

  /**
   * Method to validate configuration
   */
  public void validateConfiguration() {
    log.info("TripAdvisor API Configuration:");
    log.info("API Key: {}", apiKey != null ? "Set (length: " + apiKey.length() + ")" : "Not set");
    log.info("API Host: {}", apiHost);
    log.info("WebClient base URL: {}", tripAdvisorWebClient.toString());
  }
}
