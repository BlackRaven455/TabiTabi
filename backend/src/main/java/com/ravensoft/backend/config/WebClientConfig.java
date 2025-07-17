package com.ravensoft.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

  @Bean
  public WebClient tripAdvisorWebClient(@Value("${tripadvisor.api.key}") String apiKey,
                                        @Value("${tripadvisor.api.host}") String apiHost) {
    return WebClient.builder()
      .baseUrl("https://" + apiHost)
      .defaultHeader("X-RapidAPI-Key", apiKey)
      .defaultHeader("X-RapidAPI-Host", apiHost)
      .defaultHeader("Content-Type", "application/json")
      .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
      .build();
  }
}
