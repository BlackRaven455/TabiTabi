package com.ravensoft.backend.services;

import com.ravensoft.backend.dao.UserRepository;
import com.ravensoft.backend.entity.User;
import com.ravensoft.backend.controllers.AuthController.RegisterRequest;
import com.ravensoft.backend.entity.Userpreference;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

    return new org.springframework.security.core.userdetails.User(
      user.getEmail(),
      user.getPasswordHash(),
      getAuthorities(user)
    );
  }

  /**
   * Check if user exists by email
   */
  public boolean existsByEmail(String email) {
    return userRepository.findByEmail(email).isPresent();
  }

  /**
   * Create a new user from registration request
   */
  public User createUser(RegisterRequest registerRequest) {
    if (registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty()) {
      throw new IllegalArgumentException("Email cannot be empty");
    }
    if (registerRequest.getPassword() == null || registerRequest.getPassword().length() < 6) {
      throw new IllegalArgumentException("Password must be at least 6 characters long");
    }
    if (existsByEmail(registerRequest.getEmail())) {
      throw new IllegalArgumentException("User with this email already exists");
    }

    User user = new User();
    user.setEmail(registerRequest.getEmail().toLowerCase().trim());
    user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
    user.setFirstName(registerRequest.getFirstName());
    user.setLastName(registerRequest.getLastName());
    user.setEnabled(true);

    return userRepository.save(user);
  }

  /**
   * Find user by email
   */
  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  /**
   * Get user by ID
   */
  public Optional<User> findById(Long id) {
    return userRepository.findById(Math.toIntExact(id));
  }

  /**
   * Update user information
   */
  public User updateUser(User user) {
    return userRepository.save(user);
  }

  /**
   * Delete user by ID
   */
  public void deleteUser(Long id) {
    userRepository.deleteById(Math.toIntExact(id));
  }

  /**
   * Change user password
   */
  public void changePassword(String email, String oldPassword, String newPassword) {
    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
      throw new IllegalArgumentException("Current password is incorrect");
    }

    if (newPassword == null || newPassword.length() < 6) {
      throw new IllegalArgumentException("New password must be at least 6 characters long");
    }

    user.setPasswordHash(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }

  /**
   * Enable or disable user account
   */
  public void setUserEnabled(String email, boolean enabled) {
    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    user.setEnabled(enabled);
    userRepository.save(user);
  }

  /**
   * Get authorities for a user ToDO can be extended for role-based authorization)
   */
  private List<SimpleGrantedAuthority> getAuthorities(User user) {
    return List.of(new SimpleGrantedAuthority("ROLE_USER"));
  }

  /**
   * Get total number of users
   */
  public long getUserCount() {
    return userRepository.count();
  }


  /**
   * Check if user account is enabled
   */
  public boolean isUserEnabled(String email) {
    return userRepository.findByEmail(email)
      .map(User::getEnabled)
      .orElse(false);
  }

  public Integer findUserIdByEmail(String email) {
    return userRepository.findByEmail(email)
      .map(User::getId)
      .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
  }
}
