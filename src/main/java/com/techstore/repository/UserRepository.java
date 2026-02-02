package com.techstore.repository;

import com.techstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //Select * from users where email = ?
    Optional<User> findByEmail(String email);
}
