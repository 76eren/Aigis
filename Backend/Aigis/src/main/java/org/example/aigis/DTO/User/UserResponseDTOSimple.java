package org.example.aigis.DTO.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.aigis.Model.Role;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTOSimple {
    private UUID id;
    private String username;
    private String usernameUnique;
    private String about;
    private Role role;
    private String profilePictureId;
}
