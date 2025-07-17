package com.ravensoft.backend.exceptions;

public class PlaceNotFoundException extends RuntimeException {
  public PlaceNotFoundException(String message) {
    super(message);
  }

  public PlaceNotFoundException(int placeId) {
    super("Place with id " + placeId + " not found");
  }
}
