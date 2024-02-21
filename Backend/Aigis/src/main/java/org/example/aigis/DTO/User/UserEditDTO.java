package org.example.aigis.DTO.User;

import org.example.aigis.Model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEditDTO {
    private String username;
    private String usernameUnique;
    private String password;
    private Role role;
    private String about;
}
