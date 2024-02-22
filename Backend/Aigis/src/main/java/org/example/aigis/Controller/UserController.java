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
import org.example.aigis.Service.UserVerification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    private final UserVerification userVerification;
    private final ImageDao imageDao;

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

    @GetMapping(value = "/{usernameUnique}")
    public ApiResponse<UserResponseDTO> getUserByUsernameUnique(
            @PathVariable String usernameUnique) {
        Optional<User> user = userDAO.findByUsernameUnique(usernameUnique);
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
            @PathVariable String usernameUnique,
            Authentication authentication) throws IOException {

        String imageExtention = multipartFile.getOriginalFilename().split("\\.")[1];

        Optional<User> user = userDAO.findByUsernameUnique(usernameUnique);
        if (user.isEmpty()) {
            return new ApiResponse<>("User not found", HttpStatus.NOT_FOUND);
        }

        if (!userVerification.verifyUser(authentication, user.get().getUsernameUnique())) {
            return new ApiResponse<>("You are not authorized to edit this user", HttpStatus.UNAUTHORIZED);
        }

        for (SupportedExtensions i : SupportedExtensions.values()) {
            if (i.getExtension().substring(1).equalsIgnoreCase(imageExtention)) {
                Image image = imageDao.saveImage(multipartFile, i);
                user.get().setProfilePicture(image);
                userDAO.save(user.get());
                return new ApiResponse<>("Profile picture updated", HttpStatus.OK);
            }
        }

        return new ApiResponse<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping(path = {"/{id}"})
    public ApiResponse<UserResponseDTO> editUser(
            @PathVariable("id") UUID id,
            @RequestBody UserEditDTO userEditDTO,
            Authentication authentication
    ) {
        Optional<User> foundUser = userDAO.findById(id);
        if(foundUser.isEmpty()) {
            return new ApiResponse<>("User not found", HttpStatus.NOT_FOUND);
        }

        User user = foundUser.get();

        if (!userVerification.verifyUser(authentication, user.getUsernameUnique())) {
            return new ApiResponse<>("You are not authorized to edit this user", HttpStatus.UNAUTHORIZED);
        }

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


