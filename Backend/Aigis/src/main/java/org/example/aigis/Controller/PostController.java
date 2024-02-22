package org.example.aigis.Controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.aigis.DTO.POST.PostCreateDTO;
import org.example.aigis.DTO.POST.PostGetDTO;
import org.example.aigis.Dao.ImageDao;
import org.example.aigis.Dao.PostDao;
import org.example.aigis.Dao.UserDAO;
import org.example.aigis.Mapper.PostMapper;
import org.example.aigis.Model.*;
import org.example.aigis.Service.UserVerification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(value = "/api/v1/post")
@RequiredArgsConstructor
public class PostController {
    private final PostDao postDao;
    private final UserVerification userVerification;
    private final UserDAO userDAO;
    private final PostMapper postMapper;
    private final ImageDao imageDao;

    @PostMapping(value = "/create/{usernameUnique}", consumes = "multipart/form-data")
    public ApiResponse<PostCreateDTO> createPost(
            @PathVariable String usernameUnique,
            @RequestParam("post") String postCreateDTOStr,
            @RequestParam(value = "image", required = false) MultipartFile multipartFile,
            Authentication authentication) throws IOException {

        PostCreateDTO postCreateDTO = new ObjectMapper().readValue(postCreateDTOStr, PostCreateDTO.class);
        Optional<User> user = this.userDAO.findByUsernameUnique(usernameUnique);
        if (user.isEmpty()) {
            return new ApiResponse<>(null, "User not found", HttpStatus.NOT_FOUND);
        }

        if (!userVerification.verifyUser(authentication, usernameUnique)) {
            return new ApiResponse<>(null, "Error", HttpStatus.BAD_REQUEST);
        }

        Image image = null;
        if (multipartFile != null) {
            String extention = multipartFile.getOriginalFilename().split("\\.")[1];
            for (SupportedExtensions i : SupportedExtensions.values()) {
                if (i.getExtension().substring(1).equalsIgnoreCase(extention)) {
                    image = imageDao.saveImage(multipartFile, i);
                    break;
                }
            }
        }
        return postDao.createPost(String.valueOf(user.get().getId()), postCreateDTO, image);
    }

    @GetMapping(value = "/{usernameUnique}")
    public ApiResponse<List<PostGetDTO>> getPostsFromUser(@PathVariable String usernameUnique) {
        Optional<User> user = this.userDAO.findByUsernameUnique(usernameUnique);
        if (user.isEmpty()) {
            return new ApiResponse<>(null, "User not found", HttpStatus.NOT_FOUND);
        }

        List<Post> posts = user.get().getPosts();
        List<PostGetDTO> postsGet = new ArrayList<>();
        for (Post post : posts) {
            postsGet.add(postMapper.fromEntityToPostGetDto(post));
        }

        return new ApiResponse<>(postsGet, "Posts", HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ApiResponse<?> deletePost(@PathVariable String id, Authentication authentication) {
        Optional<Post> post = this.postDao.findById(UUID.fromString(id));
        if (post.isEmpty()) {
            return new ApiResponse<>(null, "Post not found", HttpStatus.NOT_FOUND);
        }

        if (!userVerification.verifyUser(authentication, post.get().getUser().getUsernameUnique())) {
            return new ApiResponse<>(null, "Error", HttpStatus.BAD_REQUEST);
        }

        postDao.deletePost(post.get());
        return new ApiResponse<>(null, "Post deleted", HttpStatus.OK);
    }

}
