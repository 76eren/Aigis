package org.example.aigis.DTO.POST;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostGetDTO {
    private String content;
    private int likes;
    private long date;
}
