package com.ravensoft.backend.controllers;

import com.ravensoft.backend.services.UserService;
import com.ravensoft.backend.services.JwtService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final UserService userService;
  private final JwtService jwtService;

  public AuthController(AuthenticationManager authenticationManager,
                        UserService userService,
                        JwtService jwtService) {
    this.authenticationManager = authenticationManager;
    this.userService = userService;
    this.jwtService = jwtService;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

    try {
      Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
          loginRequest.getEmail(),
          loginRequest.getPassword()
        )
      );

      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      String token = jwtService.generateToken(userDetails);

      Map<String, String> response = new HashMap<>();
      response.put("token", token);
      response.put("type", "Bearer");
      response.put("message", "Login successful");

      System.out.println(response);
      return ResponseEntity.ok(response);

    } catch (AuthenticationException e) {
      Map<String, String> errorResponse = new HashMap<>();
      errorResponse.put("error", "Invalid credentials");
      return ResponseEntity.status(401).body(errorResponse);
    }
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
    try {
      if (userService.existsByEmail(registerRequest.getEmail())) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "User with this email already exists");
        return ResponseEntity.badRequest().body(errorResponse);
      }

      userService.createUser(registerRequest);

      Map<String, String> response = new HashMap<>();
      response.put("message", "User registered successfully");
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      Map<String, String> errorResponse = new HashMap<>();
      errorResponse.put("error", "Registration failed: " + e.getMessage());
      return ResponseEntity.badRequest().body(errorResponse);
    }
  }

  @PostMapping("/me")
  public ResponseEntity<?> me(@RequestHeader("Authorization") String authHeader) {
    try {
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("error", "Missing or invalid Authorization header"));
      }

      String token = authHeader.substring(7);
      String email = jwtService.extractUsername(token);

      Map<String, String> response = new HashMap<>();
      response.put("email", email);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(Map.of("error", "User not authenticated: " + e.getMessage()));
    }
  }


  @Setter
  @Getter
  public static class LoginRequest {
    private String email;
    private String password;


    public LoginRequest() {}

    public LoginRequest(String email, String password) {
      this.email = email;
      this.password = password;
    }


  }

  @Setter
  @Getter
  public static class RegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;


    public RegisterRequest() {}

    public RegisterRequest(String email, String password, String firstName, String lastName) {
      this.email = email;
      this.password = password;
      this.firstName = firstName;
      this.lastName = lastName;
    }

  }
}
