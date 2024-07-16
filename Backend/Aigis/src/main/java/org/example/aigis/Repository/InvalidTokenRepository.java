package org.example.aigis.Repository;

import org.example.aigis.Model.InvalidToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidTokenRepository extends JpaRepository<InvalidToken, String> {
}