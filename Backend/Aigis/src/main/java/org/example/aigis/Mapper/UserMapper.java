package org.example.aigis.Mapper;


import org.example.aigis.DTO.User.UserResponseDTO;
import org.example.aigis.DTO.User.UserResponseDTOSimple;
import org.example.aigis.Model.Image;
import org.example.aigis.Model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper {
    public UserResponseDTO fromEntity(User user) {
        String profilePictureId = null;
        if (user.getProfilePicture() != null) {
            profilePictureId = String.valueOf(user.getProfilePicture().getId());
        }

        List<UserResponseDTOSimple> following = new ArrayList<>();
        for (User i : user.getFollowing()) {
            following.add(UserResponseDTOSimple
                    .builder()
                    .id(i.getId())
                    .username(i.getUsername())
                    .usernameUnique(i.getUsernameUnique())
                    .about(i.getAbout())
                    .role(i.getRole())
                    .profilePictureId(i.getProfilePicture() != null ? String.valueOf(i.getProfilePicture().getId()) : null)
                    .build());
        }

        List<UserResponseDTOSimple> followers = new ArrayList<>();
        for (User i : user.getFollowers()) {
            followers.add(UserResponseDTOSimple
                    .builder()
                    .id(i.getId())
                    .username(i.getUsername())
                    .usernameUnique(i.getUsernameUnique())
                    .about(i.getAbout())
                    .role(i.getRole())
                    .profilePictureId(i.getProfilePicture() != null ? String.valueOf(i.getProfilePicture().getId()) : null)
                    .build());
        }

        return UserResponseDTO
                .builder()
                .id(user.getId())
                .usernameUnique(user.getUsernameUnique())
                .username(user.getUsername())
                .role(user.getRole())
                .about(user.getAbout())
                .profilePictureId(profilePictureId)
                .following(following)
                .followers(followers)
                .build();

    }
}
