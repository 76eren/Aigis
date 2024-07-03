package org.example.aigis.DTO.Auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthPasswordTokenResponseDTO {
    private boolean isTokenValid;
}
