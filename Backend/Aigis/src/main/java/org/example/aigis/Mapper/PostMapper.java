package org.example.aigis.Mapper;

import lombok.RequiredArgsConstructor;
import org.example.aigis.DTO.POST.PostCreateDTO;
import org.example.aigis.DTO.POST.PostGetDTO;
import org.example.aigis.Model.Post;
import org.springframework.stereotype.Component;

import java.util.UUID;


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
        UUID imageId = null;
        if (post.getImage() != null) {
            imageId = post.getImage().getId();
        }

        return PostGetDTO
                .builder()
                .content(post.getContent())
                .likes(post.getLikes())
                .date(post.getDate())
                .id(post.getId())
                .imageId(imageId)
                .build();
    }
}
