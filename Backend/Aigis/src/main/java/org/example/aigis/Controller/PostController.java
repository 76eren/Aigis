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
import org.example.aigis.Service.PostService;
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
    private final UserDAO userDAO;
    private final PostService postService;

    @PostMapping(value = "/create/{usernameUnique}", consumes = "multipart/form-data")
    public ApiResponse<PostCreateDTO> createPost(
            @PathVariable String usernameUnique,
            @RequestParam("post") String postCreateDTOStr,
            @RequestParam(value = "image", required = false) MultipartFile multipartFile) throws IOException {

        Optional<User> user = this.userDAO.findByUsernameUnique(usernameUnique);
        if (user.isEmpty()) {
            return new ApiResponse<>(null, "User not found", HttpStatus.NOT_FOUND);
        }

        PostService.PostData postData = this.postService.createPost(multipartFile, postCreateDTOStr, usernameUnique);


        return postDao.createPost(String.valueOf(user.get().getId()), postData.getPostCreateDTO(), postData.getImage());
    }

    @GetMapping(value = "/{usernameUnique}")
    public ApiResponse<List<PostGetDTO>> getPostsFromUser(@PathVariable String usernameUnique) {
        Optional<User> user = this.userDAO.findByUsernameUnique(usernameUnique);
        if (user.isEmpty()) {
            return new ApiResponse<>(null, "User not found", HttpStatus.NOT_FOUND);
        }

        return new ApiResponse<>(this.postService.getPostsFromUser(user), "Posts", HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ApiResponse<?> deletePost(@PathVariable String id) {
        Optional<Post> post = this.postDao.findById(UUID.fromString(id));
        if (post.isEmpty()) {
            return new ApiResponse<>(null, "Post not found", HttpStatus.NOT_FOUND);
        }


        postDao.deletePost(post.get());
        return new ApiResponse<>(null, "Post deleted", HttpStatus.OK);
    }

}
