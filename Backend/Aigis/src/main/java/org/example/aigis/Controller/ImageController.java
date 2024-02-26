package org.example.aigis.Controller;


import lombok.RequiredArgsConstructor;
import org.example.aigis.DTO.Image.ImageGetDTO;
import org.example.aigis.Dao.ImageDao;
import org.example.aigis.Mapper.ImageMapper;
import org.example.aigis.Model.ApiResponse;
import org.example.aigis.Model.Image;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/image")
@RequiredArgsConstructor
public class ImageController {
    private final ImageDao imageDao;
    private final ImageMapper imageMapper;

    @GetMapping(value = "/{id}")
    public ApiResponse<ImageGetDTO> getImageById(@PathVariable("id") UUID id) {
        try {
            byte[] imageBytes = imageDao.getImageById(id);
            Image image = imageDao.getImageDetailsById(id);
            ImageGetDTO imageGetDTO = imageMapper.toImageDetailGetDTO(image, imageBytes);
            if (imageBytes == null) {
                return new ApiResponse<>(null, HttpStatus.NOT_FOUND);
            }
            return new ApiResponse<>(imageGetDTO);
        }
        catch (Exception e) {
            return new ApiResponse<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/direct/{id}")
    public ResponseEntity<byte[]> getImageDirectlyById(@PathVariable("id") UUID id) {
        try {
            byte[] imageBytes = imageDao.getImageById(id);
            if (imageBytes == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
