package com.ravensoft.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_gen")
  @SequenceGenerator(name = "users_id_gen", sequenceName = "users_user_id_seq", allocationSize = 1)
  @Column(name = "user_id", nullable = false)
  private Integer id;

  @Column(name = "first_name", nullable = false, length = 50)
  private String firstName;

  @Column(name = "last_name", nullable = false, length = 50)
  private String lastName;

  @Column(name = "email", nullable = false, length = 100)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "created_at")
  private Instant createdAt;

  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "updated_at")
  private Instant updatedAt;

  @OneToMany(mappedBy = "user")
  private Set<Booking> bookings = new LinkedHashSet<>();

  @OneToMany(mappedBy = "user")
  private Set<Recommendation> recommendations = new LinkedHashSet<>();

  @OneToMany(mappedBy = "user")
  private Set<Userpreference> userpreferences = new LinkedHashSet<>();

  @ColumnDefault("true")
  @Column(name = "enabled")
  private Boolean enabled = true;

}
