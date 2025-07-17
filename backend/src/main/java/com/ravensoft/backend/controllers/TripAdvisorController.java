package com.ravensoft.backend.controllers;

import com.ravensoft.backend.services.RapidapiService;
import com.ravensoft.backend.services.TripAdvisorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class TripAdvisorController {

  private final TripAdvisorService tripAdvisorService;
  private final RapidapiService rapidapiService;

  @PostMapping("/fetch-attractions")
  public Mono<Integer> fetchAndSaveAttractions(@RequestParam String geoId,
                                               @RequestParam String startDate,
                                               @RequestParam String endDate) {
    return tripAdvisorService.fetchAndSaveAttractions(geoId, startDate, endDate);
  }
  @PostMapping("/test")
  public Mono<String> getPlaces(@RequestParam String id,
                                @RequestParam String start,
                                @RequestParam String end) {
    return rapidapiService.getPlaces(id, start, end);
  }
}
