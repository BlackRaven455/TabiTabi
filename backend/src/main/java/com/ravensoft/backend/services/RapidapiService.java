package com.ravensoft.backend.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ravensoft.backend.dao.CategoryRepository;
import com.ravensoft.backend.dao.PlacesRepository;
import com.ravensoft.backend.entity.Places;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RapidapiService {
  private final PlacesRepository placesRepository;
  private final CategoryRepository categoryRepository;
  private final ObjectMapper objectMapper;
  private final WebClient rapidWebClient;
  @Value("${tripadvisor.api.key}")
  private String apiKey;

  @Value("${tripadvisor.api.host}")
  private String apiHost;



  public Mono<String> getPlaces(String tripAdvisorId, String startDate, String endDate) {
    log.info("Fetching attractions for geoId: {}, dates: {} to {}", tripAdvisorId, startDate, endDate);
    return rapidWebClient.get()
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
        }
      )
      .bodyToMono(String.class)
      .doOnSubscribe(subscription -> log.debug("Starting API call to TripAdvisor"))
      .doOnNext(response -> {
        log.debug("Received response from TripAdvisor API");

        // Пишем JSON в отдельный файл
        try {
          String filePath = "logs/response.json";
          Files.write(
            Paths.get(filePath),
            response.getBytes(),
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
          );
          log.info("JSON response successfully written to {}", filePath);
        } catch (IOException e) {
          log.error("Failed to write JSON response to file", e);
        }
      });
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
}
