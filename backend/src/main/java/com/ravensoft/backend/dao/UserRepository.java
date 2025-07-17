package com.ravensoft.backend.dao;

import com.ravensoft.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface  UserRepository  extends JpaRepository<User, Integer> {
  Optional<User> findByEmail(String email);

}
