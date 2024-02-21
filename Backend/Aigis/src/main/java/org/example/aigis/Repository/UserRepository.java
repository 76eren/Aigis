package org.example.aigis.Repository;

import org.example.aigis.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameUnique(String usernameUnique);
    Optional<Set<User>> findAllByUsername(String username);
}
