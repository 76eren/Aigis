package org.example.aigis.Controller;


import org.example.aigis.Dao.ImageDao;
import org.example.aigis.Dao.UserDAO;
import org.example.aigis.Model.Image;
import org.example.aigis.Model.SupportedExtensions;
import org.example.aigis.Service.AuthenticationService;
import org.example.aigis.DTO.Auth.AuthRegisterDTO;
import org.example.aigis.DTO.Auth.AuthResponseDTO;
import org.example.aigis.DTO.User.UserEditDTO;
import org.example.aigis.DTO.User.UserResponseDTO;
import org.example.aigis.Mapper.UserMapper;
import org.example.aigis.Model.ApiResponse;
import org.example.aigis.Model.User;
import lombok.RequiredArgsConstructor;
import org.example.aigis.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final UserService userService;

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

    @GetMapping(value = "/{userIdentifier}")
    public ApiResponse<UserResponseDTO> getUsernameByIdentifier(
            @PathVariable String userIdentifier) {

        Optional<User> user = userDAO.findByUnkownIdentifier(userIdentifier);

        if (user.isEmpty()) {
            return new ApiResponse<>("User not found", HttpStatus.NOT_FOUND);
        }
        return new ApiResponse<>(userMapper.fromEntity(user.get()));
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

    @PatchMapping(value = "/{usernameUnique}")
    public ApiResponse<?> assignProfilePicture(
            @RequestParam(value = "image", required = true) MultipartFile multipartFile,
            @PathVariable String usernameUnique
    ) throws IOException {


        Optional<User> user = userDAO.findByUsernameUnique(usernameUnique);
        if (user.isEmpty()) {
            return new ApiResponse<>("User not found", HttpStatus.NOT_FOUND);
        }

        Optional<User> newUser = this.userService.assignProfilePicture(multipartFile, user);
        if (newUser.isEmpty()) {
            return new ApiResponse<>("Invalid image", HttpStatus.BAD_REQUEST);
        }

        this.userDAO.save(newUser.get());
        return new ApiResponse<>("Profile picture updated", HttpStatus.OK);
    }

    // TOOD: This is horrible, will rewrite later
    @PutMapping(path = {"/{id}"})
    public ApiResponse<UserResponseDTO> editUser(
            @PathVariable("id") UUID id,
            @RequestBody UserEditDTO userEditDTO
    ) {
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

    @PatchMapping(value = "/follow/{usernameUnique}")
    public ApiResponse<UserResponseDTO> followUser(
            @PathVariable String usernameUnique) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal().toString().isEmpty()){
            return new ApiResponse<>("User not found", HttpStatus.NOT_FOUND);
        }

        User user = userDAO.findById(UUID.fromString(authentication.getName())).get();

        return this.userDAO.followUser(user, usernameUnique);
    }

    @GetMapping(path = {"/me"})
    public ApiResponse<UserResponseDTO> getMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal().toString().isEmpty()){
            return new ApiResponse<>("User not found", HttpStatus.NOT_FOUND);
        }
        return new ApiResponse<>(userMapper.fromEntity(userDAO.findById(UUID.fromString(authentication.getPrincipal().toString())).orElseThrow()));
    }
}


