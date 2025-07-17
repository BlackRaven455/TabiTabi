package com.ravensoft.backend.config;

import com.ravensoft.backend.entity.Category;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;

@Configuration
public class RestConfig implements RepositoryRestConfigurer {
  public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
    HttpMethod[] unsupportedActions = { HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE};
    config.getExposureConfiguration()
    .forDomainType(Category.class)
      .withItemExposure((metdata, httpMethods) -> httpMethods.disable(unsupportedActions))
      .withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(unsupportedActions));
  }
}
