package org.example.aigis.DTO.Auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRegisterDTO {
    private String usernameUnique;
    private String username;
    private String about;
    private String password;
}
