package org.example.aigis.Mapper;

import lombok.RequiredArgsConstructor;
import org.example.aigis.DTO.POST.PostCreateDTO;
import org.example.aigis.Model.Post;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PostMapper {
    public PostCreateDTO fromEntity(Post post) {
        return PostCreateDTO
                .builder()
                .content(post.getContent())
                .build();
    }
}
