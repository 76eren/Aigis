package org.example.aigis.DTO.Image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageGetDTO {
    private String extention;
    private byte[] image;
}
