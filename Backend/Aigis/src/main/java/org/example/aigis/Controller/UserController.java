package org.example.aigis.Controller;


import org.example.aigis.Dao.UserDAO;
import org.example.aigis.Service.AuthenticationService;
import org.example.aigis.DTO.Auth.AuthRegisterDTO;
import org.example.aigis.DTO.Auth.AuthResponseDTO;
import org.example.aigis.DTO.User.UserEditDTO;
import org.example.aigis.DTO.User.UserResponseDTO;
import org.example.aigis.Mapper.UserMapper;
import org.example.aigis.Model.ApiResponse;
import org.example.aigis.Model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserDAO userDAO;
    private final UserMapper userMapper;
    private final AuthenticationService authenticationService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    @ResponseBody
    public ApiResponse<List<UserResponseDTO>> getUsers() {
        List<UserResponseDTO> res = userDAO
            .findAll()
            .stream()
            .map(userMapper::fromEntity)
            .toList();

        return new ApiResponse<>(res);
    }

    @PostMapping(value = "/register")
    public ApiResponse<AuthResponseDTO> register(@RequestBody AuthRegisterDTO loginDTO) {
        Optional<String> tokenResponse = authenticationService.register(loginDTO.getUsernameUnique(), loginDTO.getUsername(), loginDTO.getPassword(), loginDTO.getAbout());

        if (tokenResponse.isEmpty()) {
            return new ApiResponse<>("User already exists", HttpStatus.BAD_REQUEST);
        }

        String token = tokenResponse.get();
        return new ApiResponse<>(new AuthResponseDTO(token));
    }

    // TODO: MAKE THIS CHECK IF THE USER USER CALLING THIS IS ACTUALLY THE USER THEY ARE TRYING TO EDIT
    @PutMapping(path = {"/{id}"})
    public ApiResponse<UserResponseDTO> editUser(@PathVariable("id") UUID id, @RequestBody UserEditDTO userEditDTO) {
        Optional<User> foundUser = userDAO.findById(id);
        if(foundUser.isEmpty()) {
            return new ApiResponse<>("User not found", HttpStatus.NOT_FOUND);
        }

        User user = foundUser.get();

        if (userEditDTO.getUsername() != null) {
            user.setUsername(userEditDTO.getUsername());
        }

        if (userEditDTO.getRole() != null) {
            user.setRole(userEditDTO.getRole());
        }

        if (userEditDTO.getAbout() != null) {
            user.setAbout(userEditDTO.getAbout());
        }

        if (userEditDTO.getPassword() != null) {
            user.setPassword(userEditDTO.getPassword());
        }

        // TODO: Make this a separate API call
        if (userEditDTO.getUsernameUnique() != null) {
            // We check if this usernameUnique already exists
            Optional<User> userWithUsernameUnique = userDAO.findByUsernameUnique(userEditDTO.getUsernameUnique());
            if (userWithUsernameUnique.isPresent()) {
                return new ApiResponse<>("UsernameUnique already exists", HttpStatus.BAD_REQUEST);
            }
            user.setUsernameUnique(userEditDTO.getUsernameUnique());
        }

        User createdUser = userDAO.save(user);
        return new ApiResponse<>(userMapper.fromEntity(createdUser));
    }
}


