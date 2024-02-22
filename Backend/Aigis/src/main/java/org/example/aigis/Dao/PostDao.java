package org.example.aigis.Dao;


import lombok.RequiredArgsConstructor;
import org.example.aigis.DTO.POST.PostCreateDTO;
import org.example.aigis.Mapper.PostMapper;
import org.example.aigis.Model.ApiResponse;
import org.example.aigis.Model.Post;
import org.example.aigis.Model.User;
import org.example.aigis.Repository.PostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PostDao {
    private final PostRepository postRepository;
    private final UserDAO userDAO;
    private final PostMapper postMapper;

    public ApiResponse<PostCreateDTO> createPost(String id, PostCreateDTO postCreateDTO) {
        Post post = Post.builder()
                .content(postCreateDTO.getContent())
                .likes(0)
                .date(System.currentTimeMillis())
                .build();

        UUID myId = UUID.fromString(id);
        Optional<User> user = this.userDAO.findById(myId);
        if (user.isPresent()) {
            post.setUser(this.userDAO.findById(myId).get());
            return new ApiResponse<>(postMapper.fromEntityToPostCreateDto(postRepository.save(post)), "Post created", HttpStatus.CREATED);
        }
        return new ApiResponse<>(null, "An unknown error has occured", HttpStatus.BAD_REQUEST);
    }
}
