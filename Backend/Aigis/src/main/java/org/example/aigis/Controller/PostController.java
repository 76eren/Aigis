package org.example.aigis.Controller;


import lombok.RequiredArgsConstructor;
import org.example.aigis.DTO.POST.PostCreateDTO;
import org.example.aigis.Dao.PostDao;
import org.example.aigis.Dao.UserDAO;
import org.example.aigis.Model.ApiResponse;
import org.example.aigis.Model.User;
import org.example.aigis.Service.UserVerification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/post")
@RequiredArgsConstructor
public class PostController {
    private final PostDao postDao;
    private final UserVerification userVerification;
    private final UserDAO userDAO;

    @PostMapping(value = "/create/{id}")
    public ApiResponse<PostCreateDTO> createPost(@PathVariable String id, @RequestBody PostCreateDTO postCreateDTO, Authentication authentication) {
        Optional<User> user = this.userDAO.findById(UUID.fromString(id));
        if (user.isEmpty()) {
            return new ApiResponse<>(null, "User not found", HttpStatus.NOT_FOUND);
        }


        if (!userVerification.verifyUser(authentication, this.userDAO.findById(UUID.fromString(id)).get().getUsernameUnique())) {
            return new ApiResponse<>(null, "Error", HttpStatus.BAD_REQUEST);
        }

        return postDao.createPost(id, postCreateDTO);
    }

}
