package com.ravensoft.backend.exceptions;

public class CategoryNotFoundException extends RuntimeException {
  public CategoryNotFoundException(String message) {
    super(message);
  }

  public CategoryNotFoundException(int categoryId) {
    super("Category with id " + categoryId + " not found");
  }
}
