package org.example.aigis.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "invalid_tokens")
public class InvalidToken {
    @Id
    private String token;
}
