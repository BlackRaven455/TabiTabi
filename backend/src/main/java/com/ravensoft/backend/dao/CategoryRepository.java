package com.ravensoft.backend.dao;

import com.ravensoft.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "category", path = "place-category")
public interface CategoryRepository extends JpaRepository<Category, Integer> {
  Optional<Category> findByName(String categoryName);
}
