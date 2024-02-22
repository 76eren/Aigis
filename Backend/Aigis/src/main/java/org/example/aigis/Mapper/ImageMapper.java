package org.example.aigis.Mapper;

import lombok.RequiredArgsConstructor;
import org.example.aigis.DTO.Image.ImageGetDTO;
import org.example.aigis.Model.Image;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageMapper {
    public ImageGetDTO toImageDetailGetDTO(Image image, byte[] imageBytes) {
        return ImageGetDTO
                .builder()
                .extention(image.getImageExtention().toString())
                .image(imageBytes)
                .build();
    }
}
