package org.example.aigis.Dao;


import org.example.aigis.DTO.User.UserResponseDTO;
import org.example.aigis.Mapper.UserMapper;
import org.example.aigis.Model.ApiResponse;
import org.example.aigis.Repository.UserRepository;
import org.example.aigis.Model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class UserDAO implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<User> findAll() {
        return userRepository.findAll();
    }
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public Optional<User> findByUsernameUnique(String usernameUnique) {
        return userRepository.findByUsernameUnique(usernameUnique);
    }
    public Optional<Set<User>> findAllByUsername(String username) {
        return userRepository.findAllByUsername(username);
    }
    public User save(User user) {
        return userRepository.save(user);
    }
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameUnique(username).orElseThrow();
    }

    public ApiResponse<UserResponseDTO> followUser(User user, String usernameUnique) {
        List<User> users = user.getFollowing();
        User toFollow = users
                .stream()
                .filter(u -> u.getUsernameUnique().equals(usernameUnique))
                .findFirst()
                .orElse(null);

        if (toFollow != null) {
            users.remove(toFollow);

            List<User> followers = toFollow.getFollowers();
            followers.remove(user);
            toFollow.setFollowers(followers);

            this.userRepository.save(toFollow);
            this.userRepository.save(toFollow);
        }
        else {
            Optional<User> userOptional = userRepository.findByUsernameUnique(usernameUnique);
            if (userOptional.isEmpty()) {
                return new ApiResponse<>("User not found");
            }
            User newUser = userOptional.get();
            users.add(newUser);

            List<User> followers = newUser.getFollowers();
            if (followers == null) {
                followers = new ArrayList<>();
            }

            followers.add(user);
            newUser.setFollowers(followers);

            this.userRepository.save(newUser);
        }

        return new ApiResponse<>(this.userMapper.fromEntity(userRepository.save(user)));
    }

    public Optional<User> findByUnkownIdentifier(String userIdentifier) {
        // We can get the user by both @ and UUID
        boolean isUUID = false;
        try{
            UUID uuid = UUID.fromString(userIdentifier);
            return this.findById(UUID.fromString(userIdentifier));
        }
        catch (IllegalArgumentException ignored) {
            return this.findByUsernameUnique(userIdentifier);
        }

    }
}
