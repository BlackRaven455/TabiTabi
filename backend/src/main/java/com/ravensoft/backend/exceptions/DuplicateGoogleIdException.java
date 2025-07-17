package com.ravensoft.backend.exceptions;

public class DuplicateGoogleIdException extends RuntimeException {
  public DuplicateGoogleIdException(String googleId) {
    super("Place with Google ID " + googleId + " already exists");
  }
}
