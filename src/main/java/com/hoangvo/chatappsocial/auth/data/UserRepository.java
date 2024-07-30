package com.hoangvo.chatappsocial.auth.data;

import com.hoangvo.chatappsocial.auth.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
}
