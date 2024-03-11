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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
        }
        else {
            Optional<User> userOptional = userRepository.findByUsernameUnique(usernameUnique);
            if (userOptional.isEmpty()) {
                return new ApiResponse<>("User not found");
            }
            users.add(userOptional.get());
        }

        return new ApiResponse<>(this.userMapper.fromEntity(userRepository.save(user)));
    }
}
