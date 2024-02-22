package org.example.aigis.Mapper;

import lombok.RequiredArgsConstructor;
import org.example.aigis.DTO.POST.PostCreateDTO;
import org.example.aigis.DTO.POST.PostGetDTO;
import org.example.aigis.Model.Post;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PostMapper {
    public PostCreateDTO fromEntityToPostCreateDto(Post post) {
        return PostCreateDTO
                .builder()
                .content(post.getContent())
                .build();
    }

    public PostGetDTO fromEntityToPostGetDto(Post post) {
        return PostGetDTO
                .builder()
                .content(post.getContent())
                .likes(post.getLikes())
                .date(post.getDate())
                .id(post.getId())
                .imageId(post.getImage().getId())
                .build();
    }
}
