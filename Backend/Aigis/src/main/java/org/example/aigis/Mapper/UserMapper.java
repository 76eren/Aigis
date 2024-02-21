package org.example.aigis.Mapper;


import org.example.aigis.DTO.User.UserResponseDTO;
import org.example.aigis.Model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    public UserResponseDTO fromEntity(User user) {
        return UserResponseDTO
                .builder()
                .id(user.getId())
                .usernameUnique(user.getUsernameUnique())
                .username(user.getUsername())
                .role(user.getRole())
                .about(user.getAbout())
                .build();
    }
}
