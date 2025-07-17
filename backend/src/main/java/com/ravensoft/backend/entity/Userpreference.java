package com.ravensoft.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "userpreferences")
public class Userpreference {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userpreferences_id_gen")
  @SequenceGenerator(name = "userpreferences_id_gen", sequenceName = "userpreferences_preference_id_seq", allocationSize = 1)
  @Column(name = "preference_id", nullable = false)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "place_id", nullable = false)
  private Places place;

  @Column(name = "is_liked", nullable = false)
  private Boolean isLiked = false;

  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "created_at")
  private Instant createdAt;

}
