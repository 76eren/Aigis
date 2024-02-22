package org.example.aigis.DTO.POST;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostGetDTO {
    private UUID id;
    private String content;
    private int likes;
    private long date;
    private UUID imageId;
}
